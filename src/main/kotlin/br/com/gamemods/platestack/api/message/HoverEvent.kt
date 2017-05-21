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

package br.com.gamemods.platestack.api.message

import br.com.gamemods.platestack.api.message.Text.RawText
import br.com.gamemods.platestack.api.minecraft.item.ItemStack
import br.com.gamemods.platestack.api.server.PlateStack
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/**
 * An action that happens when a player points the pointer to a [Text]
 */
sealed class HoverEvent(val action: String) {
    protected abstract val value: Any

    /**
     * A text will be shown inside a tooltip.
     * @property text The text to be show. The [Text.hoverEvent] and [Text.clickEvent] will be ignored.
     *
     * @constructor Creates this event using any [Text]
     * @param text See [text]
     */
    class ShowText(val text: Text): HoverEvent("show_text") {
        constructor(text: String): this(RawText(text))

        override val value: Text get() = text
    }

    class ShowItem(val serializedItem: JsonObject): HoverEvent("show_item") {
        constructor(serializedItem: String): this(JsonParser().parse(serializedItem).asJsonObject)
        constructor(stack: ItemStack): this(PlateStack.internal.createShowItemJSON(stack))

        override val value: JsonObject get() = serializedItem
    }

    class ShowAchievement(val id: String): HoverEvent("show_achievement") {
        override val value: String get() = id
    }

    class ShowEntity(val serializedEntity: JsonObject): HoverEvent("show_entity") {
        override val value: JsonObject get() = serializedEntity
    }

    /**
     * The hover event will be equals only if it deeply matches all properties
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as HoverEvent

        if (action != other.action) return false

        return true
    }

    /**
     * Calculates the hashcode for this event
     */
    override fun hashCode(): Int {
        return action.hashCode()
    }

    /**
     * Returns a verbose string
     */
    override fun toString(): String {
        return "HoverEvent(action='$action')"
    }
}
