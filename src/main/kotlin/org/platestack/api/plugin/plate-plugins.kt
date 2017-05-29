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
import org.objectweb.asm.*
import org.platestack.api.message.Text
import org.platestack.api.message.Translator
import org.platestack.api.plugin.annotation.Plate
import org.platestack.api.plugin.version.Version
import org.platestack.api.server.PlateServer
import org.platestack.api.server.PlateStack
import org.platestack.api.server.PlatformNamespace
import org.platestack.api.server.internal.InternalAccessor
import java.io.File
import java.io.IOException
import java.lang.reflect.Modifier
import java.net.URL
import java.net.URLClassLoader
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

/**
 * A plugin loaded by PlateStack
 */
abstract class PlatePlugin: Plugin {
    override val namespace: PlateNamespace get() = PlateNamespace

    val metadata = checkNotNull(PlateNamespace.loadingPlugin) {
        "Attempted to create a new PlatePlugin instance in a invalid state!"
    }

    override val version get() = metadata.version

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
    internal var loadingPlugin: PlateMetadata? = null ; get() {
        val value = field
        field = null
        return value
    }

    val loadedPlugins: Collection<PlatePlugin> get() = plugins.values.toList()

    override fun get(pluginId: String) = plugins[pluginId]

    private fun <T: PlatePlugin> getOrCreateInstance(`class`: KClass<T>): T {
        `class`.objectInstance?.apply { return this }
        return `class`.createInstance()
    }

    inline private fun JarInputStream.forEachEntry(action: (JarEntry) -> Unit) {
        while (null != nextJarEntry?.also(action)) {
            // Action already executed
        }
    }

    private val plateDescriptor = Type.getDescriptor(Plate::class.java)
    private object Abort: Throwable()

    @Throws(IOException::class)
    private fun scan(file: URL): Collection<String> {
        val classesToLoad = ArrayDeque<String>()
        file.openStream().use { JarInputStream(it).use { input ->
            input.forEachEntry { entry ->
                if(!entry.isDirectory && entry.name.endsWith(".class", ignoreCase = true)) {
                    var className: String? = null
                    var public = false
                    var annotated = false
                    val node = object : ClassVisitor(Opcodes.ASM5) {
                        override fun visitAnnotation(desc: String?, visible: Boolean): AnnotationVisitor? {
                            if (desc == plateDescriptor) {
                                annotated = true
                                throw Abort
                            }

                            return super.visitAnnotation(desc, visible)
                        }

                        override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
                            className = name
                            public = Modifier.isPublic(access)
                            super.visit(version, access, name, signature, superName, interfaces)
                        }
                    }

                    val reader = ClassReader(input)
                    try {
                        reader.accept(node, 0)
                    }
                    catch (ignored: Abort) {
                        // We just want to read the annotations and the class header, not the entire class.
                    }

                    if (public && annotated && className == entry.name.let { it.substring(0, it.length-6) }) {
                        classesToLoad += Type.getType("L$className;").className
                    }
                }
            }
        } }

        return classesToLoad
    }

    private data class LoadingClass(val file: URL, val name: String, val classLoader: PluginClassLoader, var kClass: KClass<out PlatePlugin>, var metadata: PlateMetadata)

    private fun loadClasses(file: URL, classNames: Collection<String>): Map<String, LoadingClass?> {
        if(classNames.isEmpty())
            return emptyMap()

        val pluginClass = PlatePlugin::class
        val loader = PluginClassLoader(file, javaClass.classLoader)
        return classNames.asSequence()
                .map {
                    println("Loading the class $it from $file")
                    it to loader.loadClass(it).kotlin
                }
                .map { (name, `class`) ->

                    if(`class`.isSubclassOf(pluginClass)) {
                        val annotation = `class`.findAnnotation<Plate>()
                        if(annotation == null) {
                            println("The class $`class` is not contains @Plate annotation and will not be loaded!")
                            name to null
                        }
                        else {
                            @Suppress("UNCHECKED_CAST")
                            name to LoadingClass(file, name, loader, `class` as KClass<out PlatePlugin>, PlateMetadata(annotation))
                        }
                    }
                    else {
                        println("The class $`class` does not extends ${pluginClass.simpleName} and will not be loaded!")
                        name to null
                    }

                }
                .toMap()
    }

    fun load(files: Set<URL>): List<PlatePlugin> {
        val loadingClasses = files.asSequence()
                .map { it to scan(it) }
                .map { (url, classes) -> loadClasses(url, classes) }
                .flatMap { it.asSequence() }
                .map { (name, loading) -> name to loading }
                .toMap()

        val loadingPlugins = loadingClasses.asSequence()
                .mapNotNull { it.value }
                .associate { it.metadata.id to it }

        val order = PlateStack.internal.resolveOrder(loadingClasses.values.mapNotNull { it?.metadata })

        return order.map {
            val data = loadingPlugins[it.id] ?: error("Could not find the plugin data for ${it.id}.\n\nOrder: $order\n\nLoading plugins: $loadingPlugin\n\nLoading classes: $loadingClasses\n\nURLs: $files")
            println("Loading ${it.name} ${it.version} -- #${it.id} ${data.kClass}")
            synchronized(this) {
                try {
                    loadingPlugin = it
                    getOrCreateInstance(data.kClass)
                }
                finally {
                    loadingPlugin = null
                }
            }
        }
    }

    fun load(vararg files: URL) = load(files.toSet())
}

class PluginClassLoader(url: URL, parent: ClassLoader): URLClassLoader(arrayOf(url), parent)

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

    PlateNamespace.load(File("D:\\_InteliJ\\CleanDishes\\001 Simple Hello World\\Java\\build\\libs\\001 Simple Hello World - Java-0.1.0-SNAPSHOT.jar").toURI().toURL())
}
