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

package org.platestack.api.plugin

import com.google.gson.JsonObject
import org.platestack.api.message.Text
import org.platestack.api.message.Translator
import org.platestack.api.plugin.version.Version
import org.platestack.api.server.PlateServer
import org.platestack.api.server.PlateStack
import org.platestack.api.server.PlatformNamespace
import org.platestack.api.server.UniqueModification
import org.platestack.api.server.internal.InternalAccessor
import java.io.File
import java.net.URL
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * A plugin loaded by PlateStack
 */
abstract class PlatePlugin: Plugin {
    override val namespace: PlateNamespace get() = PlateNamespace

    val metadata = checkNotNull(PlateNamespace.loader.loadingPlugin) {
        "Attempted to create a new PlatePlugin instance in a invalid state!"
    }

    override val version get() = metadata.version

    /**
     * A logger to be used exclusively by this plugin
     */
    @Suppress("LeakingThis")
    protected val logger = PlateStack.internal.createLogger(this)
}

abstract class PlateLoader {
    /**
     * A plugin which is being loaded right now
     */
    internal var loadingPlugin: PlateMetadata? = null ; get() {
        val value = field
        field = null
        return value
    }

    protected fun <T: PlatePlugin> getOrCreateInstance(metadata: PlateMetadata, `class`: KClass<T>): T {
        synchronized(this) {
            try {
                println("Loading ${metadata.name} ${metadata.version} -- #${metadata.id} $`class`")
                loadingPlugin = metadata
                `class`.objectInstance?.apply { return this }
                return `class`.createInstance()
            }
            finally {
                loadingPlugin = null
            }
        }
    }

    abstract fun load(files: Set<URL>): List<PlatePlugin>
    fun load(vararg files: URL) = load(files.toSet())
}

/**
 * All PlateStack plugins must be loaded from here
 */
object PlateNamespace: PluginNamespace {
    var loader by UniqueModification<PlateLoader>()

    override val id: String get() = "plate"

    /**
     * All loaded plugins
     */
    private val plugins = hashMapOf<String, PlatePlugin>()

    val loadedPlugins: Collection<PlatePlugin> get() = plugins.values.toList()

    override fun get(pluginId: String) = plugins[pluginId]
}

fun main(args: Array<String>) {
    PlateStack = object : PlateServer{
        override val platformName = "test"
        override lateinit var translator: Translator
        override val platform = PlatformNamespace("test" to Version(0,1,0,"SNAPSHOT"))
        @Suppress("OverridingDeprecatedMember")
        override val internal = object : InternalAccessor {
            override fun toJson(text: Text): JsonObject {
                TODO("not implemented")
            }

            override fun resolveOrder(metadata: Collection<PlateMetadata>) = metadata.toList()
        }
    }

    PlateNamespace.loader.load(File("D:\\_InteliJ\\CleanDishes\\001 Simple Hello World\\Java\\build\\libs\\001 Simple Hello World - Java-0.1.0-SNAPSHOT.jar").toURI().toURL())
}
