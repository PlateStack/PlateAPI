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

/**
 * A phrase which is visible to the players
 * @property namespace The group which this message belongs to. Empty namespaces disables translations.
 * @property id An unique identifier of this title inside the namespace. Empty IDs disables translations.
 * @property content The message that will be displayed. May contains tokens, for example: "Hello {player}! Today is {date}."
 *
 * @constructor Creates a possible translatable sentence.
 * @param namespace See [namespace]
 * @param id See [id]
 * @param content See [content]
 */
data class Sentence(val namespace: String, val id: String, val content: String) {

    /**
     * Creates an untranslatable title
     */
    constructor(content: String): this("", "", content)

    companion object {
        /**
         * An empty sentence. Can be used for memory optimization.
         */
        @JvmField val EMPTY = Sentence("")
    }
}
