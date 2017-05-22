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

import br.com.gamemods.platestack.api.message.Translator
import br.com.gamemods.platestack.api.plugin.annotation.Plate
import br.com.gamemods.platestack.api.plugin.version.Version
import br.com.gamemods.platestack.api.server.PlateServer
import br.com.gamemods.platestack.api.server.PlateStack
import br.com.gamemods.platestack.api.server.PlatformNamespace
import br.com.gamemods.platestack.api.server.internal.InternalAccessor
import org.objectweb.asm.*
import java.io.File
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
import kotlin.reflect.jvm.jvmName

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
    internal var loadingPlugin: Plate? = null ; get() {
        val value = field
        field = null
        return value
    }

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
    fun load(file: URL): Collection<PlatePlugin> {
        println("PlateDescriptor: $plateDescriptor\n")

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

        if(classesToLoad.isEmpty())
            return emptyList()

        println("\n\nClasses to load: $classesToLoad")
        val loader = PluginClassLoader(file, javaClass.classLoader)
        val loadedClasses = classesToLoad.map { loader.loadClass(it).kotlin }

        println("\n\nLoaded classes: $loadedClasses")
        val pluginClass = PlatePlugin::class
        return loadedClasses.associate {
            it to it.findAnnotation<Plate>()
        }.mapNotNull { (`class`, annotation) ->
            if(annotation == null) {
                println("The class $`class` is not contains @Plate annotation and will not be loaded!")
                null
            }
            else if(`class`.isSubclassOf(pluginClass)) {
                println("Loading ${annotation.name} ${annotation.version} -- $`class`")
                @Suppress("UNCHECKED_CAST")
                synchronized(this) {
                    try {
                        loadingPlugin = annotation
                        getOrCreateInstance(`class` as KClass<out PlatePlugin>)
                    } finally {
                        loadingPlugin = null
                    }
                }
            }
            else {
                println("The class $`class` does not extends ${pluginClass.jvmName} and will not be loaded!")
                null
            }
        }
    }
}

class PluginClassLoader(url: URL, parent: ClassLoader): URLClassLoader(arrayOf(url), parent)

fun main(args: Array<String>) {
    PlateStack = object : PlateServer{
        override val platformName = "test"
        override lateinit var translator: Translator
        override val platform = PlatformNamespace("test" to Version(0,1,0,"SNAPSHOT"))
        override val internal = object : InternalAccessor {}
    }

    PlateNamespace.load(File("D:\\_InteliJ\\PlateStack\\ExamplePlugin\\build\\libs\\ExamplePlugin-0.1.0-SNAPSHOT.jar").toURI().toURL())
}
