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

import com.github.salomonbrys.kotson.contains
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonArray
import org.platestack.util.letTry

data class MojangStatus(
        val time: Long,
        private val response: JsonArray
) {
    val minecraft by lazy { get("minecraft.net") }
    val sessions by lazy { get("session.minecraft.net") }
    val account by lazy { get("account.mojang.com") }
    val auth by lazy { get("auth.mojang.com") }
    val skins by lazy { get("skins.minecraft.net") }
    val authServer by lazy { get("authserver.mojang.com") }
    val sessionServer by lazy { get("sessionserver.mojang.com") }
    val api by lazy { get("api.mojang.com") }
    val textures by lazy { get("textures.minecraft.net") }
    val mojang by lazy { get("mojang.com") }

    enum class Color {
        GREEN, YELLOW, RED, UNKNOWN
    }

    operator fun get(domain: String): Color {
        val color = response.asSequence().map { it.obj }.find { domain in it}?.get(domain)?.string ?: return Color.UNKNOWN
        return letTry { Color.valueOf(color.toUpperCase()) } ?: Color.UNKNOWN
    }
}
