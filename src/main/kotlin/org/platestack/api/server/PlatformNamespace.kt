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

import org.platestack.api.plugin.Plugin
import org.platestack.api.plugin.PluginNamespace
import org.platestack.api.plugin.version.Version

class PlatformNamespace(vararg entries: Pair<String, Version>): PluginNamespace {
    inner class Platform(override val version: Version): Plugin {
        override val namespace: PluginNamespace get() = this@PlatformNamespace
    }

    private val entries = entries.associate { (name, version)-> name to Platform(version) }

    override val id: String get() = "platform"
    override fun get(pluginId: String) = entries[pluginId]
}
