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

package org.platestack.api.server.internal

import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.set
import com.google.gson.JsonObject
import org.platestack.api.message.Text
import org.platestack.api.minecraft.item.ItemStack
import org.platestack.api.plugin.PlatePlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface InternalAccessor {
    fun createShowItemJSON(stack: ItemStack): JsonObject {
        val json = jsonObject("id" to stack.item.minecraftNamedId)
        if(stack.metadata != 0.toShort()) {
            json["Damage"] = stack.metadata
        }

        val data = stack.data
        if(data.isNotEmpty()) {
            json["tag"] = data.toJson()
        }

        return json
    }

    fun createLogger(plugin: PlatePlugin): Logger = LoggerFactory.getLogger(plugin.javaClass)

    @Deprecated("Will be renamed soon")
    fun toJson(text: Text): JsonObject
}
