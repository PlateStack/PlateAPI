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

package org.platestack.api.json

import com.github.salomonbrys.kotson.forEach
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.string
import com.google.gson.*
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonReader
import org.platestack.api.message.Message
import org.platestack.api.message.Sentence
import org.platestack.structure.immutable.ImmutableMap
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.io.Reader
import java.io.Serializable
import java.util.*

class PlateJsonReader(`in`: Reader) : JsonReader(`in`) {
    companion object {
        operator fun JsonObject.contains(key: String) = this.has(key)

        @JvmStatic fun applyCustomObjects(json: JsonElement): JsonElement {
            if(json is JsonObject) {
                if("sentence" in json && "parameters" in json) {
                    try {
                        @Suppress("UNCHECKED_CAST")
                        val message = Message(
                                json["sentence"].let { Sentence(it["namespace"].string, it["id"].string, it["content"].string) },
                                ObjectInputStream(ByteArrayInputStream(Base64.getDecoder().decode(json["parameters"].string))).readObject()
                                        as ImmutableMap<String, Serializable>
                        )
                        return JsonMessage(message)
                    } catch (e: Exception) {
                        return json
                    }
                }

                json.forEach { key, element ->
                    val custom = applyCustomObjects(element)
                    if(custom != element)
                        json.add(key, custom)
                }
            }
            else if(json is JsonArray) {
                json.forEachIndexed { index, element ->
                    if (element is JsonObject || element is JsonArray) {
                        val custom = applyCustomObjects(element)
                        if(custom != element)
                            json[index] = custom
                    }
                }
            }

            return json
        }
    }

    /**
     * Returns the next value from the JSON stream as a parse tree.
     *
     * @throws JsonParseException if there is an IOException or if the specified text is not valid JSON
     * @since 1.6
     */
    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun parse(): JsonElement {
        val lenient = isLenient
        isLenient = true
        try {
            return applyCustomObjects(Streams.parse(this))
        } catch (e: StackOverflowError) {
            throw JsonParseException("Failed parsing JSON source: $this to Json", e)
        } catch (e: OutOfMemoryError) {
            throw JsonParseException("Failed parsing JSON source: $this to Json", e)
        } finally {
            isLenient = lenient
        }
    }
}
