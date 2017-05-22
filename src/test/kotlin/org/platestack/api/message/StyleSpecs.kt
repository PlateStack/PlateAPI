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

package org.platestack.api.message

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.Assert.assertEquals
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class StyleSpecs: Spek({
    given("a bold style") {
        val bold = ChatFormat.BOLD.style
        on("copy(underline=true)") {
            val result = bold.copy(underline = true)
            it("should create a style only with bold and underline") {
                assertEquals(Style(bold = true, underline = true), result)
            }
        }

        given("a bold and underline style") {
            val boldUnderline = Style(ChatFormat.BOLD, ChatFormat.UNDERLINE)
            on("copy(strike=true)") {
                val strike = boldUnderline.copy(strike = true)
                it("should create a bold, underline and strike style") {
                    assertEquals(Style(bold = true, underline = true, strike = true), strike)
                }
            }
        }

        on("copy(ChatFormat.ITALIC)") {
            val result = bold.copy(ChatFormat.ITALIC)
            it("should create a style only with bold and italic") {
                assertEquals(Style(bold = true, italic = true), result)
            }
        }

        on("copy(ChatColor.RED, ChatFormat.ITALIC)") {
            val result = bold.copy(ChatColor.RED, ChatFormat.ITALIC)
            it("should create a style with red color, bold and italic font") {
                assertEquals(Style(ChatColor.RED, italic = true, bold = true), result)
            }
        }

        given("an italic style") {
            val italic = ChatFormat.ITALIC.style
            on("bold + italic") {
                val result = bold + italic
                it("should create a style only with bold and italic formats") {
                    assertEquals(Style(bold = true, italic = true), result)
                }
            }
        }
    }
})
