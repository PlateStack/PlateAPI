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

package org.platestack.api.player

import com.github.salomonbrys.kotson.obj
import com.google.gson.JsonParser
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.Assert.assertEquals
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.platestack.api.mojang.GameProfile
import org.platestack.api.mojang.MojangAPI
import org.platestack.api.mojang.SkinModel

@RunWith(JUnitPlatform::class)
class GameProfileSpecs: Spek({
    given("The compressed UUID 4566e69fc90748ee8d71d7ba5aa00d20") {
        on("uncompress") {
            it("should return") {
                assertEquals("4566e69f-c907-48ee-8d71-d7ba5aa00d20", MojangAPI.uncompressUUID("4566e69fc90748ee8d71d7ba5aa00d20"))
            }
        }
    }

    given("A full JSON profile") {
        on("construct") {
            it("should parse it properly") {
                val profile = GameProfile(
                        JsonParser().parse("{\"id\":\"4566e69fc90748ee8d71d7ba5aa00d20\",\"name\":\"Thinkofdeath\",\"properties\":[{\"name\":\"textures\",\"value\":\"eyJ0aW1lc3RhbXAiOjE1MDE3MjA1NTkwMjAsInByb2ZpbGVJZCI6IjQ1NjZlNjlmYzkwNzQ4ZWU4ZDcxZDdiYTVhYTAwZDIwIiwicHJvZmlsZU5hbWUiOiJUaGlua29mZGVhdGgiLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTNlODFiOWUxOWFiMWVmMTdhOTBjMGFhNGUxMDg1ZmMxM2NkNDdjZWQ1YTdhMWE0OTI4MDNiMzU2MWU0YTE1YiJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJiOWM1ZWE3NjNjODZmYzVjYWVhMzNkODJiMGZhNjVhN2MyMjhmZDMyMWJhNTQ3NjZlYTk1YTNkMGI5NzkzIn19fQ==\"}]}")
                                .obj
                )

                assertEquals("4566e69f-c907-48ee-8d71-d7ba5aa00d20", profile.uniqueId.toString())
                assertEquals("Thinkofdeath", profile.name)
                assertEquals(1501720559020L, profile.time)
                assertEquals("http://textures.minecraft.net/texture/13e81b9e19ab1ef17a90c0aa4e1085fc13cd47ced5a7a1a492803b3561e4a15b", profile.skin?.toString())
                assertEquals("http://textures.minecraft.net/texture/22b9c5ea763c86fc5caea33d82b0fa65a7c228fd321ba54766ea95a3d0b9793", profile.cape?.toString())
                assertEquals(SkinModel.STEVE, profile.skinModel)
            }
        }
    }
})