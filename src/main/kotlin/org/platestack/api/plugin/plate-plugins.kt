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

import org.platestack.api.plugin.exception.PluginLoadingException
import org.platestack.api.server.PlateStack
import org.platestack.api.server.UniqueModification
import java.lang.reflect.Modifier
import java.net.URL
import kotlin.reflect.KClass
import kotlin.reflect.full.cast
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.jvm.jvmName

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
    init {
        try {
            PlateNamespace.loader
            error("A plate plugin loader has already been installed and ${javaClass.name} can't be instantiated now.")
        }
        catch (ignored: UninitializedPropertyAccessException) {
            // Allowed
        }
    }

    private val loadingLock = Any()

    /**
     * A plugin which is being loaded right now
     */
    internal var loadingPlugin: PlateMetadata? = null ; get() {
        val value = field
        field = null
        return value
    }

    @Throws(ClassNotFoundException::class)
    protected fun ClassLoader.loadPluginClass(metadata: PlateMetadata, className: String): KClass<out PlatePlugin> {
        synchronized(loadingLock) {
            val kClass: KClass<*>
            try {
                println("Pre-loading ${metadata.name} ${metadata.version} -- #${metadata.id} $className")
                loadingPlugin = metadata
                kClass = loadClass(className).kotlin
            }
            catch (e: ClassNotFoundException) {
                throw PluginLoadingException(cause = e)
            }
            finally {
                loadingPlugin = null
            }

            if(!PlatePlugin::class.isSuperclassOf(kClass)) {
                throw ClassNotFoundException("The class ${kClass.jvmName} defines a @Plate annotation but does not extends the PlatePlugin class!")
            }

            @Suppress("UNCHECKED_CAST")
            return kClass as KClass<out PlatePlugin>
        }
    }

    private val <T: PlatePlugin> KClass<T>.scalaModule: T? get()  {
        try { java.getDeclaredField("MODULE$") } catch (e: NoSuchFieldException) { return null }.let {
            val access = it.modifiers
            if (Modifier.isPublic(access) && Modifier.isStatic(access) && it.type.name == jvmName && PlatePlugin::class.isSuperclassOf(it.type.kotlin)) {
                return cast(it.get(null))
            }
        }
        return null
    }

    private val <T: PlatePlugin> KClass<T>.groovySingleton: T? get() {
        val java = java
        try{ java.getDeclaredMethod("getInstance") } catch (e: NoSuchMethodException) { return null }.let {
            val access = it.modifiers
            if(it.returnType == java && it.exceptionTypes.isEmpty() && Modifier.isPublic(access) && Modifier.isStatic(access)) {
                return cast(it.invoke(null))
            }
        }
        return null
    }

    protected fun <T: PlatePlugin> getOrCreateInstance(metadata: PlateMetadata, kClass: KClass<T>): T {
        synchronized(loadingLock) {
            try {
                println("Loading ${metadata.name} ${metadata.version} -- #${metadata.id} $kClass")
                loadingPlugin = metadata

                // Kotlin object
                kClass.objectInstance?.apply { return this }

                // Scala module
                kClass.scalaModule?.apply { return this }

                // Groovy / Java singleton by static getInstance() method
                kClass.groovySingleton?.apply { return this }

                // Public constructor without parameters
                return kClass.createInstance()
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
