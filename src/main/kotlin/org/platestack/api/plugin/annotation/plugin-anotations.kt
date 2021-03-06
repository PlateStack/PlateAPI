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

package org.platestack.api.plugin.annotation

import org.platestack.api.plugin.RelationType
import org.platestack.api.server.PlateStack

/**
 * Indicates that a class is a PlateStack plugin.
 *
 * PlateStack will isolates plugin classes from other plugins to avoid conflicts and unexpected results, so you
 * are required to declare your dependencies if you need them.
 *
 * @property id The plugin ID.
 *
 * Not all strings are accepted, a valid ID must respect the following requirements:
 * * Be unique in the PlateStack's namespace
 * * The length must have a minimum of 3 and a maximum of 20 characters
 * * Be compatible with [PlateStack.ID_VALIDATOR]
 *
 * @property name A prettified version of the ID to be read by humans.
 *
 * The length must have a minimum of 3 and a maximum of 20 characters
 *
 * @property version The current plugin version. If you plugin does not contains a stable public API then the Major version must be 0!
 * See the [Semantic Versioning 2.0.0](http://semver.org/spec/v2.0.0.html) for guidance.
 *
 * @property relations All relations that this plugin has with other plugins, may also declare
 * relations to platform plugins or mods and to platform and Minecraft versions.
 *
 * @property groovy The Groovy version required by this plugin. Leave empty if it does not uses Groovy.
 * This will automatically add a groovy-all dependency to [requires] will be provided at runtime.
 *
 * @property scala The Scala version required by this plugin. Leave empty if it does not uses Scala.
 * This will automatically add a scala-library-all dependency to [requires] will be provided at runtime.
 *
 * @property kotlin The Kotlin version required by this plugin. Leave empty if it does not uses Scala.
 * This will automatically add a kotlin-stdlib dependency to [requires],
 * however the specified version will not be provided at runtime as all plate plugins depends on the plate api and it
 * depends on Kotlin itself, the specified version will be used to warn the server owners on logs if the plugin requires
 * a different Kotlin version then the provided version, which could result in incompatibility and errors.
 *
 * @property jdk The minimum JDK version required by this plugin. The default is "1.8" which stands to Java 8.
 *
 * @property requires Maven libraries which are required by this plugin, they will be automatically provided to the
 * plugin at runtime as long as it is available on Maven Center, JCenter or a custom maven repository preconfigured
 * by the server owner. **Transitive dependencies will NOT be included automatically at this time!**, so if you library
 * depends on another library you are required to add the other library manually for now.
 *
 * If This plugin depends on an other plugin which happens to depend on the same library as yours but in a different version,
 * the other plugin will take priority and PlateStack will provides you with a different version then the requested.
 * A warning will also be logged to notify the server owner that errors may happens due to the usage of incompatible library versions.
 *
 * If this plugin happens to be on the same JAR file as an other plugin which requires the same library but in a different version,
 * the highest version will be provided. All the libraries required by the other plugin will also be provided as the
 * provisioning is done on JAR level.
 */
@Retention(AnnotationRetention.BINARY) @Target(AnnotationTarget.CLASS) @MustBeDocumented
annotation class Plate(val id: String, val name: String, val version: Version, vararg val relations: Relation = emptyArray(),
                       val groovy: String = "", val scala: String = "", val kotlin: String = "", val jdk: String = "1.8",
                       val requires: Array<Library> = emptyArray())

/**
 * A maven coordinate.
 *
 * @property group The maven group
 * @property artifact The artifact inside the [group]
 * @property version The artifact version
 */
@Retention @Target @MustBeDocumented
annotation class Library(val group: String, val artifact: String, val version: String)

/**
 * A version following the [Semantic Versioning 2.0.0](http://semver.org/spec/v2.0.0.html) rules.
 *
 * Examples from simplest to full versions:
 * * 0.13.5 : `Version(0,13,5)`
 * * 2.8.255-beta : `Version(2,8,255,"beta")`
 * * 1.3.13-2.5.13.0.alpha.3 : `Version(1,3,13, "2","5","13","0" "alpha","3")
 * * 45.158.9-rc.3.50+build38.sha.5589 : `Version(45,158,9, "rc","3","50", metadata = "build38.sha.5589")`
 *
 * Alternatively you can specify the version as a single string on the [value] property but it is discouraged.
 *
 * @property major A number that is incremented when an incompatible API change happens. `0` means that no public or stable API is available.
 * @property minor A number that is incremented when a new functionality in a backwards-compatible manner
 * @property patch A number that is incremented when backwards-compatible bug fixes are published
 * @property label Empty label indicates that this is a stable version, non-empty indicates.
 *
 * Check the [specification item 9](http://semver.org/spec/v2.0.0.html#spec-item-9) for more information
 * @property metadata An optional build metadata. Check ths [specification item 10](http://semver.org/spec/v2.0.0.html#spec-item-10) for more information
 * @property value All other properties will be completely ignored if this property is not empty. It allows you to specify the version easier by
 * a single replacement token with your build script but may cause complications to the dependency system. Example: Version(0, value = "{build.version}")
 */
@Target @MustBeDocumented
annotation class Version(val major: Int = 0, val minor: Int = 0, val patch: Int = 0, vararg val label: String = emptyArray(), val metadata: String = "",
                         @Deprecated("Discouraged") val value: String = "")

/**
 * Declare a relation to an ID in a given version
 *
 * @property type The actual relation
 * @property id The related ID
 * @property versions The versions which matches this relation
 */
@Target @MustBeDocumented
annotation class Relation(val type: RelationType, val id: ID, vararg val versions: VersionRange = emptyArray())

/**
 * A selection of version which may have exclusions.
 *
 * @property min The minimum inclusive version which matches this range. Default is 0.0.0, which means any version before [max]
 * @property max The maximum inclusive version which matches this range.
 *
 * If this is lower then [min] then it will be ignored and all versions after [min] will be included.
 *
 * Default is 0.0.0-0, which is lower then 0.0.0
 *
 * @property exclusions Versions which will be excluded from this version range
 * @property unstable If unstable versions (which defines a label) should be included
 * @property caseSensitive If the label casing should affect the version comparison. For example, if "beta"` and "BETA" should be considered different.
 * @property dynamic When specified it will ignore the [min] and [max] fields and parse it in the same way as [SemVer Check](http://jubianchi.github.io/semver-check/)
 *
 * tilde-range and caret-range constraints are supported.
 *
 * Versions which are incompatible to the semantic versioning specifications will be converted in the same way as
 * platform plugin versions are converted.
 */
@Target @MustBeDocumented
annotation class VersionRange(
        val min: Version = Version(0), val max: Version = Version(0, label = "0"),
        vararg val exclusions: VersionRange = emptyArray(),
        val unstable: Boolean = true, val caseSensitive: Boolean = false,
        val dynamic: String = ""
)

/**
 * A ID of something.
 * @property value The ID
 * @property namespace The namespace where this ID belongs. Use `"plate"` for PlateStack plugins
 */
annotation class ID(val value: String, val namespace: String = "plate")

