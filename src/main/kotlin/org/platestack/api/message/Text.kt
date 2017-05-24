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

@file:Suppress("unused", "UNUSED_PARAMETER")

package org.platestack.api.message

import org.platestack.api.server.PlateStack
import java.io.Serializable
import java.util.*


/**
 * A text message that is rendered by the client game and displayed to the player.
 *
 * This model is based on [this definition](https://minecraft.gamepedia.com/Commands#Raw_JSON_text)
 * @property style The style that will be applied to this text and all children. All null style properties will inherit the parent's style.
 * @property hoverEvent What happens when the player passes the mouse pointer hover this text. The parent's event is inherited when this property is null.
 * @property insertion When the text is shift-clicked by a player, this string will be inserted in their chat input. It will not overwrite any existing text the player was writing.
 * @property clickEvent What happens when the player clicks this text. The parent's event is inherited when this property is null.
 */
sealed class Text(
        val style: Style,
        val hoverEvent: HoverEvent?,
        val clickEvent: ClickEvent?,
        val insertion: String?,
        vararg extra: Text
): Serializable {
    /**
     * Array which holds the extra texts, this will not return a new copy like [extra].
     *
     * **The returned array must not be changed!**
     */
    protected val internalExtra = extra.clone()

    /**
     * Additional messages that are rendered after this text but inherits the same style and events as this. A new copy is returned on every read.
     */
    val extra get() = internalExtra.clone()

    /**
     * Create a new compound concatenating both texts
     */
    operator fun plus(text: Text) = Compound(Style.EMPTY, this, text)

    /**
     * Translates and converts this text to a JSON string using the current server implementation
     */
    @Deprecated("The name causes confusion with json-extensions.kt")
    fun toJson(language: Language) = PlateStack.translator.toJson(this, language)

    /**
     * Converts this text to a JSON object with the same structure as the tellraw command
     */
    @Deprecated("The name is shadowing json-extensions.kt")
    fun toJson() = PlateStack.internal.toJson(this)

    /**
     * Compares if this text matches all style, event, extra messages and information with the given object
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Text

        if (style != other.style) return false
        if (hoverEvent != other.hoverEvent) return false
        if (clickEvent != other.clickEvent) return false
        if (!Arrays.equals(internalExtra, other.internalExtra)) return false

        return true
    }

    /**
     * Generates a hashcode based on all properties
     */
    override fun hashCode(): Int {
        var result = style.hashCode()
        result = 31 * result + (hoverEvent?.hashCode() ?: 0)
        result = 31 * result + (clickEvent?.hashCode() ?: 0)
        result = 31 * result + Arrays.hashCode(internalExtra)
        return result
    }

    /**
     * Converts this text to a verbose string
     */
    override fun toString(): String {
        return "${javaClass.simpleName}(style=$style, hoverEvent=$hoverEvent, clickEvent=$clickEvent, extra=${Arrays.toString(internalExtra)})"
    }

    /**
     * A list of texts that shares the same parent style and events.
     * @constructor Creates a compound defining all properties
     * @param style See [Text.style]
     * @param hoverEvent See [Text.hoverEvent]
     * @param clickEvent See [Text.clickEvent]
     * @param insertion See [Text.insertion]
     * @param extra See [Text.extra]
     */
    class Compound(
            style: Style,
            hoverEvent: HoverEvent?,
            clickEvent: ClickEvent?,
            insertion: String? = null,
            vararg extra: Text
    ) : Text(style, hoverEvent, clickEvent, insertion, *extra) {

        /**
         * Creates a compound without events
         * @param style See [Text.style]
         * @param insertion See [Text.insertion]
         * @param extra See [Text.extra]
         */
        constructor(style: Style, insertion: String?, vararg extra: Text) : this(style, null, null, insertion, *extra)

        /**
         * Creates a compound without events
         * @param style See [Text.style]
         * @param extra See [Text.extra]
         */
        constructor(style: Style, vararg extra: Text) : this(style, null, null, "", *extra)
    }

    /**
     * A raw text string.
     * @property text The text string which will be rendered. May contain formatting codes using section chars but
     * doing such may cause unexpected rendering issues. Lines can be broken with a simple `\n`
     *
     * @constructor Creates a raw text component
     * @param text See [text]
     * @param hoverEvent See [Text.hoverEvent]
     * @param clickEvent See [Text.clickEvent]
     * @param insertion See [Text.insertion]
     * @param extra See [Text.extra]
     */
    class RawText @JvmOverloads constructor(
            val text: String, style: Style = Style.EMPTY,
            hoverEvent: HoverEvent? = null,
            clickEvent: ClickEvent? = null,
            insertion: String? = null,
            vararg extra: Text
    ) : Text(style, hoverEvent, clickEvent, insertion, *extra) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false
            if (!super.equals(other)) return false

            other as RawText

            if (text != other.text) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + text.hashCode()
            return result
        }

        override fun toString(): String {
            return "RawText(text='$text', style=$style, hoverEvent=$hoverEvent, clickEvent=$clickEvent, extra=${Arrays.toString(internalExtra)})"
        }
    }

    /**
     * A translation key which will be translated by the client's renderer.
     *
     * @property key The translation key
     *
     * @constructor Creates a translation component
     * @param key See [key]
     * @param style See [Text.style]
     * @param hoverEvent See [Text.hoverEvent]
     * @param clickEvent See [Text.clickEvent]
     * @param insertion See [Text.insertion]
     * @param with A list of chat component arguments and/or string arguments to be used by translate
     * @param extra See [Text.extra]
     */
    class Translation @JvmOverloads constructor(
            val key: String,
            style: Style = Style.EMPTY,
            hoverEvent: HoverEvent? = null,
            clickEvent: ClickEvent? = null,
            insertion: String? = null,
            val with: List<Text> = emptyList(),
            vararg extra: Text
    ) : Text(style, hoverEvent, clickEvent, insertion, *extra) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false
            if (!super.equals(other)) return false

            other as Translation

            if (key != other.key) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + key.hashCode()
            return result
        }

        override fun toString(): String {
            return "Translation(key='$key', style=$style, hoverEvent=$hoverEvent, clickEvent=$clickEvent, extra=${Arrays.toString(internalExtra)})"
        }
    }

    /**
     * A string that can be used to display the key needed to preform a certain action.
     *
     * An example is `key.inventory` which will always display "E" unless the player has set a different key for opening their inventory.
     *
     * @since Minecraft 1.12
     *
     * @property key The key identifier
     *
     * @constructor Creates a key binding component
     * @param key See [key]
     * @param style See [Text.style]
     * @param hoverEvent See [Text.hoverEvent]
     * @param clickEvent See [Text.clickEvent]
     * @param insertion See [Text.insertion]
     * @param extra See [Text.extra]
     */
    class KeyBinding @JvmOverloads constructor(
            val key: String,
            style: Style = Style.EMPTY,
            hoverEvent: HoverEvent? = null,
            clickEvent: ClickEvent? = null,
            insertion: String? = null,
            vararg extra: Text
    ) : Text(style, hoverEvent, clickEvent, insertion, *extra) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false
            if (!super.equals(other)) return false

            other as KeyBinding

            if (key != other.key) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + key.hashCode()
            return result
        }

        override fun toString(): String {
            return "KeyBinding(key='$key', style=$style, hoverEvent=$hoverEvent, clickEvent=$clickEvent, extra=${Arrays.toString(internalExtra)})"
        }


    }

    /**
     * A player's score in an objective.
     *
     * Displays nothing if the player is not tracked in the given objective.
     *
     * @property name The name of the player whose score should be displayed.
     *
     * Selectors (such as `@p`) can be used, in addition to "fake" player names created by the scoreboard system.
     *
     * In addition, if the name is `"*"`, it will show the reader's own score
     * (for example, `/tellraw @a {score:{name:"*",objective:"obj"}}` will show every online player their own score in the "obj" objective).
     *
     * Note that non-player entity scores (such as `@e[type=Cow]`) do not show, even if the entity has been given a score in the objective.
     *
     * @property objective The internal name of the objective to display the player's score in.
     * @property value Optional. If present, this value is used regardless of what the score would have been.
     *
     * @constructor Creates a score component using translatable messages as parameters
     * @param name See [name]
     * @param objective See [objective]
     * @param style See [Text.style]
     * @param hoverEvent See [Text.hoverEvent]
     * @param clickEvent See [Text.clickEvent]
     * @param value See [value]
     * @param insertion See [Text.insertion]
     * @param extra See [Text.extra]
     */
    class Score @JvmOverloads constructor(
            val name: Message,
            val objective: Message,
            style: Style = Style.EMPTY,
            hoverEvent: HoverEvent? = null,
            clickEvent: ClickEvent? = null,
            val value: Message? = null,
            insertion: String? = null,
            vararg extra: Text
    ) : Text(style, hoverEvent, clickEvent, insertion, *extra) {

        /**
         * Creates a score component using untranslatable strings as parameters.
         * @param name See [name]
         * @param objective See [objective]
         * @param style See [Text.style]
         * @param hoverEvent See [Text.hoverEvent]
         * @param clickEvent See [Text.clickEvent]
         * @param value See [value]
         * @param insertion See [Text.insertion]
         * @param extra See [Text.extra]
         */
        @JvmOverloads constructor(
                name: String,
                objective: String,
                style: Style = Style.EMPTY,
                hoverEvent: HoverEvent? = null,
                clickEvent: ClickEvent? = null,
                value: String? = null,
                insertion: String? = null,
                vararg extra: Text
        ) : this(Message(name), Message(objective), style, hoverEvent, clickEvent, value?.let { Message(it) }, insertion, *extra)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false
            if (!super.equals(other)) return false

            other as Score

            if (name != other.name) return false
            if (objective != other.objective) return false
            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + name.hashCode()
            result = 31 * result + objective.hashCode()
            result = 31 * result + (value?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return "Score(name='$name', objective='$objective', value=$value, style=$style, hoverEvent=$hoverEvent, clickEvent=$clickEvent, extra=${Arrays.toString(internalExtra)})"
        }
    }

    /**
     * A string containing a selector (`@p`,`@a`,`@r`, or `@e ) and, optionally, selector arguments.
     *
     * Unlike text, the selector will be translated into the correct player/entity names.
     *
     * If more than one player/entity is detected by the selector, it will be displayed
     * in a form such as 'Name1 and Name2' or 'Name1, Name2, Name3, and Name4'.
     *
     * Clicking a player's name inserted into a /tellraw command this way will suggest a command to whisper to that player.
     * Shift-clicking a player's name will insert that name into chat.
     * Shift-clicking a non-player entity's name will insert its UUID into chat.
     * @property selector The selector string
     *
     * @constructor Constructs a selector component using a translatable message
     * @param selector See [Selector]
     * @param style See [Text.style]
     * @param hoverEvent See [Text.hoverEvent]
     * @param clickEvent See [Text.clickEvent]
     * @param insertion See [Text.insertion]
     * @param extra See [Text.extra]
     */
    class Selector @JvmOverloads constructor(
            val selector: Message,
            style: Style = Style.EMPTY,
            hoverEvent: HoverEvent? = null,
            clickEvent: ClickEvent? = null,
            insertion: String? = null,
            vararg extra: Text
    ) : Text(style, hoverEvent, clickEvent, insertion, *extra) {

        /**
         * Creates a selector component using an untranslatable message
         * @param selector See [Selector]
         * @param style See [Text.style]
         * @param hoverEvent See [Text.hoverEvent]
         * @param clickEvent See [Text.clickEvent]
         * @param extra See [Text.extra]
         */
        @JvmOverloads constructor(selector: String,
                                  style: Style = Style.EMPTY,
                                  hoverEvent: HoverEvent? = null,
                                  clickEvent: ClickEvent? = null,
                                  insertion: String? = null,
                                  vararg extra: Text
        ) : this(Message(selector), style, hoverEvent, clickEvent, insertion, *extra)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false
            if (!super.equals(other)) return false

            other as Selector

            if (selector != other.selector) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + selector.hashCode()
            return result
        }

        override fun toString(): String {
            return "Selector(selector='$selector', style=$style, hoverEvent=$hoverEvent, clickEvent=$clickEvent, extra=${Arrays.toString(internalExtra)})"
        }
    }

    class FormattedMessage @JvmOverloads constructor(
            val message: Message,
            style: Style = Style.EMPTY,
            hoverEvent: HoverEvent? = null,
            clickEvent: ClickEvent? = null,
            insertion: String? = null,
            vararg extra: Text
    ): Text(style, hoverEvent, clickEvent, insertion, *extra) {

        @JvmOverloads constructor(
                sentence: Sentence,
                style: Style = Style.EMPTY,
                parameters: Map<String, Serializable> = emptyMap(),
                hoverEvent: HoverEvent? = null,
                clickEvent: ClickEvent? = null,
                insertion: String? = null,
                vararg extra: Text
        ): this(Message(sentence, parameters), style, hoverEvent, clickEvent, insertion, *extra)

        @JvmOverloads constructor(
                rawText: String,
                style: Style = Style.EMPTY,
                hoverEvent: HoverEvent? = null,
                clickEvent: ClickEvent? = null,
                insertion: String? = null,
                vararg extra: Text
        ): this(Message(rawText), style, hoverEvent, clickEvent, insertion, *extra)
    }
}
