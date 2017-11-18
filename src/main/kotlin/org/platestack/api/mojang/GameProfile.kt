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
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.net.URL
import java.util.*

enum class SkinModel { STEVE, ALEX }

data class GameProfile(
        val uniqueId: UUID,
        val name: String,
        val time: Long = System.currentTimeMillis(),
        val skinModel: SkinModel = SkinModel.STEVE,
        val skin: URL? = null,
        val cape: URL? = null
) {
    private constructor(uniqueId: UUID, json: JsonObject,  decoded: JsonObject?, textures: JsonObject? = decoded?.get("textures")?.obj) :
            this(
                    uniqueId,
                    json["name"].string,
                    decoded?.get("timestamp")?.long ?: System.currentTimeMillis(),
                    textures?.get("SKIN")?.obj.let { skin ->
                        when(skin) {
                            null -> if(uniqueId.hashCode() and 1 != 0) SkinModel.ALEX else SkinModel.STEVE
                            else -> when(skin.obj["model"]?.string) {
                                "slim" -> SkinModel.ALEX
                                else -> SkinModel.STEVE
                            }
                        }
                    },
                    textures?.get("SKIN")?.get("url")?.string?.let(::URL),
                    textures?.get("CAPE")?.get("url")?.string?.let(::URL)
            )

    constructor(json: JsonObject) :
            this(
                    MojangAPI.uncompressUUID(json["id"].string),
                    json,
                    json["properties"]?.array?.asSequence()?.find {
                        it.obj.get("name")?.string == "textures" && "value" in it.obj
                    }?.obj?.get("value")?.string?.let {
                        JsonParser()
                                .parse(InputStreamReader(ByteArrayInputStream(Base64.getDecoder().decode(it))))
                                .obj
                    }
            )
}
