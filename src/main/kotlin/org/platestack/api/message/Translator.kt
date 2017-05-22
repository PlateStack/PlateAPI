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
 * An object which translates and format messages that will be visible to players or humans
 */
interface Translator {

    /**
     * Converts and translates a given text to a JSON message in a given language.
     * @param text The text to be converted and translated
     * @param language The target language
     * @return The translated JSON string appropriated to the current Minecraft's version
     */
    fun toJson(text: Text, language: Language): String
}