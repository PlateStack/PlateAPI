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

package br.com.gamemods.platestack.api.plugin

import br.com.gamemods.platestack.api.plugin.annotation.Plate
import br.com.gamemods.platestack.api.plugin.version.Version
import br.com.gamemods.platestack.api.server.PlateStack

/**
 * A plugin loaded by PlateStack
 */
abstract class PlatePlugin: Plugin {
    override val namespace: PlateNamespace get() = PlateNamespace

    /**
     * The [Plate] annotation which was decorating the class
     */
    val annotation = checkNotNull(PlateNamespace.loadingPlugin) {
        "Attempted to create a new PlatePlugin instance in a invalid state!"
    }

    override val version = Version.from(annotation.version)

    /**
     * A logger to be used exclusively by this plugin
     */
    @Suppress("LeakingThis")
    protected val logger = PlateStack.internal.createLogger(this)
}

/**
 * All PlateStack plugins must be loaded from here
 */
object PlateNamespace: PluginNamespace {
    override val id: String get() = "plate"

    /**
     * All loaded plugins
     */
    private val plugins = hashMapOf<String, PlatePlugin>()

    /**
     * A plugin which is being loaded right now
     */
    internal var loadingPlugin: Plate? = null

    override fun get(pluginId: String) = plugins[pluginId]
}
