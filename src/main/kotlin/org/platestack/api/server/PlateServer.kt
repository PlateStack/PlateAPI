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

package org.platestack.api.server

import org.platestack.api.message.Translator
import org.platestack.api.plugin.PlateNamespace
import org.platestack.api.plugin.Plugin
import org.platestack.api.plugin.PluginNamespace
import org.platestack.api.plugin.version.Version
import org.platestack.api.server.internal.InternalAccessor

/**
 * The server implementation
 */
interface PlateServer {
    /**
     * The name of the platform which this server is running.
     */
    val platformName: String

    /**
     * The version of the platform which this server is running
     */
    val platformVersion: Version get() = checkNotNull(platform[platformName]) {
        "This server has platformName=$platformName but platform[platformName] returns null!"
    }.version

    /**
     * The translation implementation used by this server
     */
    val translator: Translator

    /**
     * The platform namespace
     */
    val platform: PluginNamespace

    val minecraft: Plugin get() = checkNotNull(platform["minecraft"]) { "The Minecraft platform is missing!" }

    /**
     * Gets a registered namespace by its value
     */
    fun getNamespace(id: String): PluginNamespace? = when(id) {
        "plate" -> PlateNamespace
        "platform" -> platform
        else -> null
    }

    /**
     * Gets a plugin by its value and its namespace value
     */
    fun getPlugin(pluginId: String, namespaceId: String = "plate") = getNamespace(namespaceId)?.get(pluginId)

    /**
     * Used by PlateAPI to grab information or execute procedures which may vary from platform.
     *
     * Must not be directly used by plugins.
     */
    val internal: InternalAccessor
}
