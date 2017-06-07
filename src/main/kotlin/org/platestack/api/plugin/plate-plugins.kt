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

import mu.KLogger
import org.platestack.api.plugin.exception.PluginLoadingException
import org.platestack.api.server.PlateStack
import org.platestack.api.server.UniqueModification
import org.platestack.api.structure.ReflectionTarget
import org.platestack.structure.immutable.ImmutableList
import java.io.IOException
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
abstract class PlatePlugin @ReflectionTarget constructor(): Plugin {
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

    /**
     * Method called when the plugin is fully ready to load
     */
    open protected fun onEnable() {}

    /**
     * Method called when the plugin is being disable, no more activities will be allowed after this method returns
     */
    open protected fun onDisable() {}

    @JvmSynthetic internal fun enable() = onEnable()
    @JvmSynthetic internal fun disable() = onEnable()
}

abstract class PlateLoader(protected val logger: KLogger) {
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
    abstract val loadingOrder: ImmutableList<String>

    open protected fun setAPI(metadata: PlateMetadata) {
        synchronized(loadingLock) {
            try {
                loadingPlugin = metadata
                PlateNamespace.api = object : PlatePlugin(){}
            }
            finally {
                loadingPlugin = null
            }

            logger.info { "The PlateStack API has been set to ${metadata.version} and depends on the following libraries:\n - ${metadata.libraries.joinToString("\n - ")}" }
        }
    }

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
                logger.info { "Pre-loading ${metadata.name} ${metadata.version} -- #${metadata.id} $className" }
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
            if (Modifier.isPublic(access) && Modifier.isStatic(access) && it.type.name == jvmName && isSuperclassOf(it.type.kotlin)) {
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
                logger.info { "Loading ${metadata.name} ${metadata.version} -- #${metadata.id} $kClass" }
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

    protected fun enable(plugin: PlatePlugin) {
        synchronized(loadingLock) {
            if (plugin.metadata.id in PlateNamespace.plugins)
                error("The plugin ${plugin.metadata.id} is already loaded!")

            logger.info { "Enabling ${plugin.metadata.name} ${plugin.metadata.version} -- #${plugin.metadata.id}" }
            try {
                PlateNamespace.plugins[plugin.metadata.id] = plugin
                plugin.enable()
            }
            catch (e: Exception) {
                PlateNamespace.plugins -= plugin.metadata.id
                throw PluginLoadingException(cause = e)
            }
        }
    }

    protected fun disable(plugin: PlatePlugin) {
        synchronized(loadingLock) {
            if (plugin.metadata.id !in PlateNamespace.plugins)
                error("The plugin ${plugin.metadata.id} is not loaded!")

            logger.info { "Disabling ${plugin.metadata.name} ${plugin.metadata.version} -- #${plugin.metadata.id}" }
            try {
                plugin.disable()
            }
            catch (e: Exception) {
                throw PluginLoadingException(cause = e)
            }
            finally {
                PlateNamespace.plugins -= plugin.metadata.id
            }
        }
    }

    @Throws(IOException::class)
    abstract fun scan(file: URL): Map<String, PlateMetadata>

    @Throws(PluginLoadingException::class)
    abstract fun load(files: Set<URL>): List<PlatePlugin>

    fun load(vararg files: URL) = load(files.toSet())
}

/**
 * All PlateStack plugins must be loaded from here
 */
object PlateNamespace: PluginNamespace {
    var loader by UniqueModification<PlateLoader>()
    var api by UniqueModification<PlatePlugin>()

    override val id: String get() = "plate"

    /**
     * All loaded plugins
     */
    internal val plugins = hashMapOf<String, PlatePlugin>()

    val loadedPlugins: Collection<PlatePlugin> get() = plugins.values.toList()

    override fun get(pluginId: String) = plugins[pluginId]
}
