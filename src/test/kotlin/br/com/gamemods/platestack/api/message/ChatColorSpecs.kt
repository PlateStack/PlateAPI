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

package br.com.gamemods.platestack.api.message

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.Assert.assertEquals
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class ChatColorSpecs: Spek({
    given("a red color") {
        val color = ChatColor.RED
        on("toString()") {
            val str = color.toString()
            it("should return \u00A7c") {
                assertEquals("\u00A7c", str)
            }
        }

        given("a bold format") {
            val style = ChatFormat.BOLD
            on("red + bold") {
                it("should create a style with red color and bold format ") {
                    assertEquals(Style(color, bold = true), color + style)
                }
            }
        }
    }
})
