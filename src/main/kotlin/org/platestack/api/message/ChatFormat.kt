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

/**
 * Chat modifications supported by Minecraft.
 *
 * As defined in [Minecraft's wiki](https://minecraft.gamepedia.com/Formatting_codes#Formatting_codes).
 *
 * Note: The "reset" format is being considered a color: [ChatColor.RESET].
 * @property id The ID used to apply this format on JSON messages
 * @property code The character that identifies this color
 * @property title The translatable name
 * @property style A style which only sets this color. May be used for memory optimizations.
 */
enum class ChatFormat(val id: String, val code: Char, val style: Style, val title: Sentence) {
    /** Replaces each char with random chars with the same size in every rendering tick **/
    OBFUSCATED("obfuscated", 'k', Style(obfuscated = true), "Obfuscated"),

    /** Makes all characters larger and stronger **/
    BOLD("bold", 'l', Style(bold = true), "Bold"),

    /** Draws a line cutting the text horizontally **/
    STRIKE("strikethrough", 'm', Style(strike = true), Sentence("chatformat", "strike", "Strike Through")),

    /** Draws a line under the text **/
    UNDERLINE("underlined", 'n', Style(underline = true), "Underline"),

    /** Draws the text cursively **/
    ITALIC("italic", 'o', Style(italic = true), "Italic")
    ;

    @Suppress("unused")
    constructor(id: String, code: Char, style: Style, name: String):
            this(id, code, style, Sentence("chatformat", name.toLowerCase(), name))

    private val str = "\u00A7$code"

    /**
     * Returns the code prefixed by an section char.
     */
    override fun toString() = str

    /**
     * Creates a style which applies both formats
     */
    operator fun plus(format: ChatFormat) = Style(null, this, format)

    /**
     * Returns a new style with this format but only if the given style does not have a color and it does not negates this format.
     */
    operator fun plus(style: Style) = if(style.color != null || style[this] != null) style else style.copy(this)
}