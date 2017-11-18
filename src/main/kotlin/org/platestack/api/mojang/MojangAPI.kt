/*
 *  Copyright (C) 2017 José Roberto de Araújo Júnior
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.platestack.api.mojang

import com.github.salomonbrys.kotson.*
import com.google.gson.*
import org.platestack.structure.immutable.immutableMapOf
import org.platestack.structure.immutable.toImmutableMap
import org.platestack.util.letTry
import org.platestack.util.tryOrNull
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object MojangAPI {
    private val cachedCalls = mutableMapOf<String, Cache>()
    private val cachePersistence = 5 * 60 * 1000

    private val callLog = sortedMapOf<Long, Int>()
    private var requestCount = 0
    private val requestLimit = 600
    private val timeLimit = 600 * 60 * 1000
    private var lastCleanup = 0L
    private val cleanupInterval = 30
    private val lock = Any()
    private val parser = JsonParser()

    fun status(ignoreCache: Boolean = false, ignoreLimit: Boolean = false) : MojangStatus {
        val call = call(url = "https://status.mojang.com/check", ignoreCache = ignoreCache, ignoreLimit = ignoreLimit)
        return MojangStatus(call.time, call.result.nullArray ?: JsonArray())
    }

    fun uniqueId(username: String, at: Long? = null, ignoreCache: Boolean = false, ignoreLimit: Boolean = false) : PlayerAccount? {
        val call = call(url = "https://api.mojang.com/users/profiles/minecraft/$username${at?.let { "?at=$at" }}", ignoreCache = ignoreCache, ignoreLimit = ignoreLimit)
        return when(call.httpCode) {
            200 -> call.result.let {
                PlayerAccount(
                        uncompressUUID(it["id"].string),
                        it["name"].string,
                        "legacy" in it.obj,
                        "demo" in it.obj
                )
            }
            204 -> null
            400 -> throw IllegalArgumentException(call.result.nullObj?.get("errorMessage")?.string ?: "Invalid timestamp.")
            else -> readError(call)
        }
    }

    fun nameHistory(uniqueId: UUID, ignoreCache: Boolean = false, ignoreLimit: Boolean = false) : SortedMap<Long, String> {
        val call = call(url = "https://api.mojang.com/user/profiles/${compressUUID(uniqueId)}/names", ignoreLimit = ignoreLimit, ignoreCache = ignoreCache)
        return when(call.httpCode) {
            200 -> call.result.array.asSequence().map { (it.obj["changedToAt"]?.long ?: 0L) to it["name"].string }.toMap(TreeMap())
            204 -> sortedMapOf()
            else -> readError(call)
        }
    }

    fun uniqueIds(usernames: Set<String>, ignoreCache: Boolean = false, ignoreLimit: Boolean = false) : Map<String, PlayerAccount?> {
        val groups = mutableListOf(mutableListOf<String>())
        var currentList = groups.first()

        val iterator = usernames.iterator()
        var count = 0
        iterator.forEachRemaining {
            currentList.add(it)
            if(count++ == 100) {
                currentList = mutableListOf()
                groups += currentList
            }
        }

        val combined = mutableMapOf<String, PlayerAccount>()

        synchronized(lock) {
            checkLimit(groups.size)
            groups.forEach { group ->
                val call = call(
                        "https://api.mojang.com/profiles/minecraft", "POST",
                        JsonArray().apply { addAll(group) }.toString().toByteArray(),
                        ignoreCache = ignoreCache, ignoreLimit = ignoreLimit
                )
                when (call.httpCode) {
                    204 -> Unit
                    200 -> {
                        call.result.array.asSequence().map { it.obj }.map {
                            PlayerAccount(
                                    uncompressUUID(it["id"].string),
                                    it["name"].string,
                                    "legacy" in it,
                                    "demo" in it
                            )
                        }.forEach { combined[it.name.toLowerCase()] = it }
                    }
                    else -> readError(call)
                }
            }
        }

        return usernames.associate { it to combined[it.toLowerCase()] }
    }

    fun gameProfile(uniqueId: UUID, ignoreCache: Boolean = false, ignoreLimit: Boolean = false) : GameProfile? {
        val call = call("https://sessionserver.mojang.com/session/minecraft/profile/${compressUUID(uniqueId)}",
                cacheTime = maxOf(60*60*1000, cachePersistence), ignoreLimit = ignoreLimit, ignoreCache = ignoreCache
        )

        return when(call.httpCode) {
            200 -> GameProfile(call.result.obj)
            204 -> null
            else -> readError(call)
        }
    }

    fun blockedServers(ignoreCache: Boolean = false, ignoreLimit: Boolean = false) : List<String> {
        val call = call("https://sessionserver.mojang.com/blockedservers", ignoreCache = ignoreCache, ignoreLimit = ignoreLimit) {
            it.letTry { it.inputStream.use { it.bufferedReader().lineSequence().toList() }.let { JsonArray().apply { addAll(it) } } }
                ?: JsonNull.INSTANCE
        }

        return when(call.httpCode) {
            200 -> call.result.array.asSequence().map { it.string }.toList()
            else -> readError(call)
        }
    }

    fun orderStatistics(
            keys: Iterable<MojangStatistics> = listOf(MojangStatistics.MINECRAFT_SOLD, MojangStatistics.PREPAID_MINECRAFT_REDEEMED),
            ignoreCache: Boolean = false, ignoreLimit: Boolean = false
    ) = orderStatistics(keys.asSequence().map { it.key }.toHashSet(), ignoreCache, ignoreLimit)

    fun orderStatistics(
            keys: Set<String> = setOf(MojangStatistics.MINECRAFT_SOLD.key, MojangStatistics.PREPAID_MINECRAFT_REDEEMED.key),
            ignoreCache: Boolean = false, ignoreLimit: Boolean = false
    ) : OrderStatistic {
        check(keys.isNotEmpty()) { "keys cannot be empty" }
        val call = call("https://api.mojang.com/orders/statistics", "POST",
                JsonObject().also {
                    it["metricKeys"] = JsonArray().apply { addAll(keys) }
                }.toString().toByteArray(),
                ignoreCache = ignoreCache, ignoreLimit = ignoreLimit
        )

        return when(call.httpCode) {
            200 -> call.result.obj.let { OrderStatistic(it["total"].int, it["last24h"].int, it["saleVelocityPerSeconds"].int) }
            else -> readError(call)
        }
    }

    fun changeSkin(uniqueId: UUID, accessToken: String, model: SkinModel, url: URL, ignoreLimit: Boolean = false) {
        val call = call(
                "https://api.mojang.com/user/profile/${compressUUID(uniqueId)}/skin", "POST",
                "model=${when(model) { SkinModel.ALEX -> "slim"; SkinModel.STEVE -> "" }}&url=${Base64.getUrlEncoder().encodeToString(url.toString().toByteArray())}".toByteArray(),
                headers = mapOf("Authorization" to "Bearer $accessToken"),
                ignoreCache = true, ignoreLimit = ignoreLimit
        )

        when(call.httpCode) {
            204 -> Unit
            else -> readError(call)
        }
    }

    fun uploadSkin(uniqueId: UUID, accessToken: String, model: SkinModel, image: ByteArray, ignoreLimit: Boolean = false) {
        val boundary = "MOJANGAPIBOUNDARY"
        val post = ByteArrayOutputStream().apply {
            write("--$boundary\nContent-Disposition: form-data; name=\"model\"\n\n${when(model){SkinModel.ALEX -> "slim"; SkinModel.STEVE -> ""}}\n--$boundary\nContent-Disposition: form-data; name=\"file\"; filename=\"skin.png\"\nContent-Type: image/png\n\n".toByteArray())
            write(image)
            write("--$boundary--".toByteArray())
        }.toByteArray()

        val call = call("https://api.mojang.com/user/profile/${compressUUID(uniqueId)}/skin", "POST",
                headers = mapOf(
                        "Authorization" to "Bearer $accessToken",
                        "Content-Length" to post.size.toString(),
                        "Content-Type" to "multipart/form-data; boundary=$boundary"
                ),
                output = post,
                ignoreCache = true,
                ignoreLimit = ignoreLimit
        )

        when(call.httpCode) {
            204 -> Unit
            else -> readError(call)
        }
    }

    fun resetSkin(uniqueId: UUID, accessToken: String, ignoreLimit: Boolean = false) {
        val call = call("https://api.mojang.com/user/profile/${compressUUID(uniqueId)}/skin", "DELETE",
                headers = mapOf("Authorization" to "Bearer $accessToken"),
                ignoreCache = true, ignoreLimit = ignoreLimit
        )

        when(call.httpCode) {
            204 -> Unit
            else -> readError(call)
        }
    }

    fun authenticate(username: String, password: String, clientToken: String? = null, game: String = "Minecraft") : MojangAuth {
        val call = call(
                "https://authserver.mojang.com/authenticate", "POST",
                JsonObject().also {
                    it["agent"] = JsonObject().also {
                        it["name"] = game
                        it["version"] = 1
                    }
                    it["username"] = username
                    it["password"] = password
                    if(clientToken != null)
                        it["clientToken"] = clientToken
                    it["requestUser"] = true
                }.toString().toByteArray(),
                countTowardLimit = false, ignoreLimit = true, ignoreCache = true
        )

        when(call.httpCode) {
            200 -> {
                val profiles = call.result.obj["availableProfiles"].nullArray?.asSequence()?.map {
                    MojangAuth.Profile(uncompressUUID(it["id"].string), it["name"].string, it["legacy"].nullBool ?: false)
                }?.map { it.id to it }?.toImmutableMap() ?: immutableMapOf()

                call.result.let {
                    return MojangAuth(
                            it["accessToken"].string,
                            it["clientToken"].nullString,
                            profiles,
                            profiles[uncompressUUID(it["selectedProfile"]["id"].string)] ?: error("The selected profile is not available"),
                            it["user"].obj.let {
                                MojangAuth.User(
                                        uncompressUUID(it["id"].string),
                                        it["properties"].array.asSequence().map { it["name"].string to it["value"].string }.toImmutableMap()
                                )
                            }
                    )
                }
            }
            else -> readError(call)
        }
    }

    fun refresh(accessToken: String, selectedProfile: MojangAuth.Profile?, clientToken: String?) : MojangAuth {
        val call = call(
                "https://authserver.mojang.com/refresh", "POST",
                JsonObject().also {
                    it["accessToken"] = accessToken
                    if(clientToken != null)
                        it["clientToken"] = clientToken
                    if(selectedProfile != null) {
                        it["selectedProfile"] = JsonObject().also {
                            it["id"] = compressUUID(selectedProfile.id)
                            it["name"] = selectedProfile.name
                        }
                    }
                    it["requestUser"] = true
                }.toString().toByteArray(),
                countTowardLimit = false, ignoreLimit = true, ignoreCache = true
        )

        when(call.httpCode) {
            200 -> {
                val profile = call.result.obj["selectedProfile"].obj.let {
                    MojangAuth.Profile(uncompressUUID(it["id"].string), it["name"].string, it["legacy"].nullBool ?: false)
                }

                call.result.let {
                    return MojangAuth(
                            it["accessToken"].string,
                            it["clientToken"].nullString,
                            immutableMapOf(profile.id to profile),
                            profile,
                            it["user"].obj.let {
                                MojangAuth.User(
                                        uncompressUUID(it["id"].string),
                                        it["properties"].array.asSequence().map { it["name"].string to it["value"].string }.toImmutableMap()
                                )
                            }
                    )
                }
            }
            else -> readError(call)
        }
    }

    fun validateAuth(accessToken: String, clientToken: String?) : Boolean {
        val call = call(
                "https://authserver.mojang.com/validate", "POST",
                JsonObject().also {
                    it["accessToken"] = accessToken
                    if(clientToken != null)
                        it["clientToken"] = clientToken
                }.toString().toByteArray(),
                countTowardLimit = false, ignoreLimit = true, ignoreCache = true
        )

        return when(call.httpCode) {
            204 -> true
            403 -> false
            else -> readError(call)
        }
    }

    fun signOut(username: String, password: String) {
        val call = call(
                "https://authserver.mojang.com/signout", "POST",
                JsonObject().also {
                    it["username"] = username
                    it["password"] = password
                }.toString().toByteArray(),
                countTowardLimit = false, ignoreLimit = true, ignoreCache = true
        )

        when(call.httpCode) {
            204 -> Unit
            else -> readError(call)
        }
    }

    fun invalidate(accessToken: String, clientToken: String?) {
        val call = call(
                "https://authserver.mojang.com/invalidate", "POST",
                JsonObject().also {
                    it["accessToken"] = accessToken
                    if(clientToken != null)
                        it["clientToken"] = clientToken
                }.toString().toByteArray(),
                countTowardLimit = false, ignoreLimit = true, ignoreCache = true
        )

        return when(call.httpCode) {
            204 -> Unit
            else -> readError(call)
        }
    }

    private fun readError(call: Cache) : Nothing {
        error(
                (tryOrNull { call.result["error"].string + " :" } ?: "Exception: ") +
                (tryOrNull { call.result["errorMessage"].string } ?: "The request failed for an unknown reason. HTTP Code: ${call.httpCode}")
        )
    }

    private fun checkLimit(requests: Int = 1) {
        if(requestCount >= requestLimit) {
            error("Rate limit exceeded!")
        }
    }

    private fun reader(conn: HttpURLConnection) : JsonElement {
        return letTry { conn.inputStream.use { parser.parse(InputStreamReader(it, conn.contentEncoding ?: "UTF-8")) } }
                ?: letTry { conn.errorStream.use { parser.parse(InputStreamReader(it, conn.contentEncoding ?: "UTF-8")) } }
                ?: JsonNull.INSTANCE
    }

    private fun call(
            url: String, method: String = "GET", output: ByteArray? = null,
            headers: Map<String, String> = emptyMap(), cacheTime: Int = cachePersistence,
            ignoreCache: Boolean = false, ignoreLimit: Boolean = false, countTowardLimit: Boolean = true,
            reader: (HttpURLConnection) -> JsonElement = this::reader
    ) : Cache {
        synchronized(lock) {
            cleanup()
            if(!ignoreCache && method == "GET") {
                cachedCalls[url]?.let { return it }
            }

            if(!ignoreLimit) {
                checkLimit()
            }

            val time = System.currentTimeMillis()
            if(countTowardLimit) {
                requestCount++
                callLog.compute(time) { _, current ->
                    (current ?: 0) + 1
                }
            }

            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = method
            conn.setRequestProperty("Content-Type", "application/json")
            conn.useCaches = false
            headers.forEach { key, value -> conn.setRequestProperty(key, value) }

            if(output?.isNotEmpty() ?: false) {
                conn.doOutput = true
                conn.outputStream.use { out->
                    out.write(output)
                }
            }

            val responseCode = conn.responseCode
            val result = Cache(
                    url,
                    reader(conn),
                    responseCode,
                    time,
                    time + cacheTime
            )

            if(method == "GET") {
                cachedCalls[url] = result
            }

            return result
        }
    }

    fun cleanup(force: Boolean = false) {
        synchronized(lock) {
            val time = System.currentTimeMillis()
            if (!force && lastCleanup + cleanupInterval > time)
                return

            val cut = time - timeLimit

            val iter = callLog.iterator()
            while (iter.hasNext()) {
                val (callTime, requests) = iter.next()
                if(callTime > cut)
                    return

                iter.remove()
                requestCount -= requests
            }

            cachedCalls.values.removeIf { time >= it.expires }

            lastCleanup = time
        }
    }

    fun compressUUID(uniqueId: UUID) = uniqueId.toString().replace("-", "")

    fun uncompressUUID(compressed: String): UUID = UUID.fromString(StringBuilder()
            .append(compressed, 0, 8)
            .append('-')
            .append(compressed, 8, 12)
            .append('-')
            .append(compressed, 12, 16)
            .append('-')
            .append(compressed, 16, 20)
            .append('-')
            .append(compressed, 20, 32)
            .toString()
    )

    private data class Cache(val url: String, val result: JsonElement, val httpCode: Int, val time: Long, val expires: Long)
}