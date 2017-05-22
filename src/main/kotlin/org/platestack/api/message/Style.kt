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

@file:Suppress("unused")
package org.platestack.api.message

import java.io.Serializable
import java.util.*

/**
 * The visual style of a text.
 *
 * All properties have a tri-state value: `true`/`null`/`false`
 *
 * A `true` value specifies that the effect is applied.  
 * A `null` value specifies that the parent style must be checked to decide if the effect is applied or not.
 * A `false` value specifies that the effect must not be applied regardless if the parent style applies it or not.
 *
 * @property color The color that will be rendered.
 *
 * A `null` value inherits the color from the parent.
 * It's differs from [ChatColor.RESET] as the reset will grab the color from the client's root renderer which may varies
 * depending on where the text is being displayed (white on chat box or black in books for example)
 * @property bold If [ChatFormat.BOLD] is applied.
 * @property italic If [ChatFormat.ITALIC] is applied.
 * @property strike If [ChatFormat.STRIKE] is applied.
 * @property underline If [ChatFormat.UNDERLINE] is applied.
 * @property obfuscated If [ChatFormat.OBFUSCATED] is applied.
 *
 * @constructor Construct a style defining the properties as boolean. All values are `null` by default.
 * @param color See [color]
 * @param bold See [bold]
 * @param italic See [italic]
 * @param strike See [strike]
 * @param underline See [underline]
 * @param obfuscated See [obfuscated]
 */
data class Style @JvmOverloads constructor(val color: ChatColor? = null,
                 val bold: Boolean? = null, val italic: Boolean? = null, val strike: Boolean? = null,
                 val underline: Boolean? = null, val obfuscated: Boolean? = null
): Serializable {
    /**
     * Creates a style specifying a color and multiple formats dynamically
     * @param color See [color]
     * @param format The formats that will be applied
     */
    constructor(color: ChatColor?, vararg format: ChatFormat): this(color,
            trueOrNull(ChatFormat.BOLD in format),
            trueOrNull(ChatFormat.ITALIC in format),
            trueOrNull(ChatFormat.STRIKE in format),
            trueOrNull(ChatFormat.UNDERLINE in format),
            trueOrNull(ChatFormat.OBFUSCATED in format)
    )

    /**
     * Creates a style specifying with a color and a single value format
     * @param color See [color]
     * @param format The format that will be applied
     */
    constructor(color: ChatColor?, format: ChatFormat): this(color,
            trueOrNull(format == ChatFormat.BOLD),
            trueOrNull(format == ChatFormat.ITALIC),
            trueOrNull(format == ChatFormat.STRIKE),
            trueOrNull(format == ChatFormat.UNDERLINE),
            trueOrNull(format == ChatFormat.OBFUSCATED)
    )

    /**
     * Creates a style specifying a color and all formats that are applied or removed
     */
    constructor(color: ChatColor?, formatMap: Map<ChatFormat, Boolean?>): this(color,
            formatMap[ChatFormat.BOLD],
            formatMap[ChatFormat.ITALIC],
            formatMap[ChatFormat.STRIKE],
            formatMap[ChatFormat.UNDERLINE],
            formatMap[ChatFormat.OBFUSCATED]
    )

    constructor(vararg format: ChatFormat): this(null, *format)
    constructor(format: ChatFormat): this(null, format)
    constructor(formatMap: Map<ChatFormat, Boolean?>): this(null, formatMap)

    companion object {
        /**
         * An empty style. May be used for memory optimizations.
         */
        @JvmField val EMPTY = Style(null)

        /**
         * Converts `true`/`false` to `true`/`null`
         * @return `true` if the [condition] is `true` or `null` otherwise
         */
        private fun trueOrNull(condition: Boolean) = if(condition) true else null
    }

    /**
     * Returns a new EnumMap containing all formats that are applied or removed. Skips undefined formats.
     */
    val formatMap: MutableMap<ChatFormat, Boolean> get() = EnumMap<ChatFormat, Boolean>(ChatFormat::class.java).apply {
        bold?.let { put(ChatFormat.BOLD, it) }
        strike?.let { put(ChatFormat.STRIKE, it) }
        italic?.let { put(ChatFormat.ITALIC, it) }
        underline?.let { put(ChatFormat.UNDERLINE, it) }
        obfuscated?.let { put(ChatFormat.OBFUSCATED, it) }
    }

    /**
     * Returns a new EnumSet containing all formats that are applied by this style. Skips undefined and removed formats.
     */
    val formatSet: MutableSet<ChatFormat> get() = EnumSet.noneOf(ChatFormat::class.java).apply {
        if(bold == true) add(ChatFormat.BOLD)
        if(strike == true) add(ChatFormat.STRIKE)
        if(italic == true) add(ChatFormat.ITALIC)
        if(underline == true) add(ChatFormat.UNDERLINE)
        if(obfuscated == true) add(ChatFormat.OBFUSCATED)
    }

    /**
     * Returns a copy of this style with a color and value format definition
     */
    fun copy(color: ChatColor? = this.color, vararg format: ChatFormat) = this + Style(color, *format)

    /**
     * Returns a copy of this style only with a value format definition
     */
    fun copy(vararg format: ChatFormat) = copy(color, *format)

    /**
     * Returns true if this style applies a given format
     */
    operator fun contains(format: ChatFormat) = when(format) {
        ChatFormat.OBFUSCATED -> obfuscated == true
        ChatFormat.BOLD -> bold == true
        ChatFormat.STRIKE -> strike == true
        ChatFormat.UNDERLINE -> underline == true
        ChatFormat.ITALIC -> italic == true
    }

    /**
     * Returns true if this style applies a given color
     */
    operator fun contains(color: ChatColor) = this.color == color

    /**
     * Gets the tri-state of a given format
     */
    operator fun get(format: ChatFormat) = when(format) {
        ChatFormat.OBFUSCATED -> obfuscated
        ChatFormat.BOLD -> bold
        ChatFormat.STRIKE -> strike
        ChatFormat.UNDERLINE -> underline
        ChatFormat.ITALIC -> italic
    }

    /**
     * Return a copy of this style with the given format applied.
     *
     * The copy will be a new instance only when needed.
     */
    operator fun plus(format: ChatFormat): Style {
        if(format in this)
            return this

        else return when(format) {
            ChatFormat.OBFUSCATED -> copy(obfuscated = true)
            ChatFormat.BOLD -> copy(bold = true)
            ChatFormat.STRIKE -> copy(strike = true)
            ChatFormat.UNDERLINE -> copy(underline = true)
            ChatFormat.ITALIC -> copy(italic = true)
        }
    }

    /**
     * Return a mix of both styles where the given style takes priority over conflicting values
     */
    operator fun plus(style: Style): Style {
        if(style == this)
            return style

        return Style(
                style.color ?: color,
                style.bold ?: bold,
                style.italic ?: italic,
                style.strike ?: strike,
                style.underline ?: underline,
                style.obfuscated ?: obfuscated
        )
    }

    /**
     * Reduces the status of a given format tri-state.
     *
     * If the current format is `true` the copy will have it set to `null`
     * If the current format is `null` the copy will have it set to `false`
     * If the current format is `false` this function will return this same instance
     */
    operator fun minus(format: ChatFormat): Style {
        val map = formatMap
        val current = map[format]
        if(current == true) {
            map -= format
            return Style(color, map)
        }
        else if(current == null) {
            map[format] = false
            return Style(color, map)
        }
        else {
            return this
        }
    }

    /**
     * Returns a copy of this style without a color only if the specified color matches the current color.
     *
     * This function returns this same instance if it differs
     */
    operator fun minus(color: ChatColor): Style {
        if(color == this.color) {
            return copy(color = null)
        } else {
            return this
        }
    }

    /**
     * Returns the legacy string that would be used to apply this style.
     * @param previous The immediate style that is being applied before.
     * @param previousColor The effective previous color that is being applied.
     */
    @JvmOverloads
    fun toLegacy(previous: Style = EMPTY, previousColor: ChatColor = previous.color ?: ChatColor.RESET): String {
        val revokeFormat = color != null && color != previousColor ||
                previous.bold == true && bold == false ||
                previous.italic == true && italic == false ||
                previous.strike == true && strike == false ||
                previous.underline == true && underline == false ||
                previous.obfuscated == true && obfuscated == false

        val sb = StringBuilder(color?.toString() ?: if(revokeFormat) previousColor.toString() else "")
        if(revokeFormat) {
            if (bold == true) sb.append(ChatFormat.BOLD)
            if (italic == true) sb.append(ChatFormat.ITALIC)
            if (strike == true) sb.append(ChatFormat.STRIKE)
            if (underline == true) sb.append(ChatFormat.UNDERLINE)
            if (obfuscated == true) sb.append(ChatFormat.OBFUSCATED)
        }
        else {
            if (bold == true && previous.bold != true) sb.append(ChatFormat.BOLD)
            if (italic == true && previous.italic != true) sb.append(ChatFormat.ITALIC)
            if (strike == true && previous.strike != true) sb.append(ChatFormat.STRIKE)
            if (underline == true && previous.underline != true) sb.append(ChatFormat.UNDERLINE)
            if (obfuscated == true && previous.obfuscated != true) sb.append(ChatFormat.OBFUSCATED)
        }
        return sb.toString()
    }
}
