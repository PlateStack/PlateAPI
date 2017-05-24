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

@file:JvmName("JsonUtil")

package org.platestack.api.json

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.platestack.api.message.Message
import org.platestack.api.message.Text

fun JsonElement.getAsMessage() = (this as? JsonPlate ?: throw UnsupportedOperationException(javaClass.simpleName)).message
fun JsonElement.getAsText() = (this as? JsonPlate ?: throw UnsupportedOperationException(javaClass.simpleName)).text

val JsonElement.message get() = getAsMessage()
val JsonElement?.nullMessage get() = this?.takeIf { !isJsonNull }?.message

val JsonElement.text get() = getAsText()
val JsonElement?.nullText get() = this?.takeIf { !isJsonNull }?.text

fun JsonObject.addProperty(property: String, value: Message) = addProperty(property, JsonMessage(value))
fun JsonObject.addProperty(property: String, value: Text) = addProperty(property, JsonText(value))

fun JsonObject.addPropertyIfNotNull(property: String, value: Message?) = value?.let { addProperty(property, it) }
fun JsonObject.addPropertyIfNotNull(property: String, value: Text?) = value?.let { addProperty(property, it) }

fun Message.toJson() = JsonMessage(this)
//TODO Fix shadowing
fun Text.toJson() = JsonText(this)

operator fun JsonElement.set(key: String, value: Any?) = obj.add(key, value.toJsonElement())
operator fun JsonElement.set(key: Int, value: Any?) = array.set(key, value.toJsonElement())

internal fun Any?.toJsonElement(): JsonElement {
    if (this == null)
        return jsonNull

    return when (this) {
        is Message -> toJson()
        is Text -> toJson()
        is Number -> toJson()
        is Char -> toJson()
        is Boolean -> toJson()
        else -> throw IllegalArgumentException("$this cannot be converted to JSON")
    }
}

// TODO Implement
interface JsonPlate {
    val message: Message get() = TODO()
    val text: Text get() = TODO()
    val element: JsonElement get() = TODO()
}
