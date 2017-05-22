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

import java.awt.Color

/**
 * Colors which Minecraft uses to format texts in the game.
 *
 * As defined on [Minecraft's wiki](https://minecraft.gamepedia.com/Formatting_codes#Color_codes)
 * @property code The character that identifies this color
 * @property title The translatable name
 * @property id Technical name
 * @property foreground Actual color that the client uses to render this color
 * @property background The shadow color
 */
enum class ChatColor(val code: Char, val title: Sentence, val id: String, val foreground: Color, val background: Color) {
    /** Black  **/
    BLACK('0', "Black", "black", 0x000000, 0x000000),

    /** Dark blue **/
    DARK_BLUE('1', "Dark Blue", "dark_blue", 0x0000AA, 0x00002A),

    /** Dark green **/
    DARK_GREEN('2', "Dark Green", "dark_green", 0x00AA00, 0x002A00),

    /** Dark aqua **/
    DARK_AQUA('3', "Dark Aqua", "dark_aqua", 0x00AAAA, 0x002A2A),

    /** Dark red **/
    DARK_RED('4', "Dark Red", "dark_red", 0xAA0000, 0x2A0000),

    /** Dark purple **/
    DARK_PURPLE('5', "Dark Purple", "dark_purple", 0xAA00AA, 0x2A002A),

    /** Gold **/
    GOLD('6', "Gold", "gold", 0xFFAA00, 0x2A2A00),

    /** Gray **/
    GRAY('7', "Gray", "gray", 0xAAAAAA, 0x2A2A2A),

    /** Dark gray **/
    DARK_GRAY('8', "Dark Gray", "dark_gray", 0x555555, 0x151515),

    /** Blue **/
    BLUE('9', "Blue", "blue", 0x5555FF, 0x15153F),

    /** Green **/
    GREEN('a', "Green", "green", 0x55FF55, 0x153F15),

    /** Aqua **/
    AQUA('b', "Aqua", "aqua", 0x55FFFF, 0x153F3F),

    /** Red **/
    RED('c', "Red", "red", 0xFF5555, 0x3F1515),

    /** Light purple **/
    LIGHT_PURPLE('d', "Light Purple", "light_purple", 0xFF55FF, 0x3F153F),

    /** Yellow **/
    YELLOW('e', "Yellow", "yellow", 0xFFFF55, 0x3F3F15),

    /** White **/
    WHITE('f', "White", "white", 0xFFFFFF, 0x3F3F3F),

    /** Resets to the Minecraft's renderer root color **/
    RESET('r', "Reset", "reset", 0xFFFFFF, 0x3F3F3F)
    ;

    /**
     * A style which only sets this color. May be used for memory optimizations.
     */
    @Suppress("LeakingThis")
    val style = Style(this)
    @Suppress("unused")
    constructor(code: Char, name: String, id: String, foreground: Int, background: Int):
            this(code, Sentence("chatcolor", id, name), id, Color(foreground), Color(background))

    /**
     * Returns the code prefixed by an section char.
     */
    override fun toString() = "\u00A7$code"

    /**
     * Return a new style with this color and the specified format.
     */
    operator fun plus(format: ChatFormat) = Style(this, format)

    /**
     * Returns a new style with this color but only if the given style does not have a color.
     */
    operator fun plus(style: Style) = if(style.color != null) style else style.copy(this)
}
