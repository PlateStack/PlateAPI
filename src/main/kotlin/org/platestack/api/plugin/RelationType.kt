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

/**
 * Defines how a plugin is related to an other
 */
enum class RelationType {
    /**
     * The declared plugin is required and must be loaded before this plugin.
     *
     * The declared plugin's event handlers will also take priority when it has the same priority as this plugin's event handlers.
     */
    REQUIRED_BEFORE,

    /**
     * The declared plugin is required but it must be loaded after this plugin.
     *
     * The declared plugin's event handlers will also have lower priority when it has the same priority as this plugin's event handlers.
     */
    REQUIRED_AFTER,

    /**
     * The declared plugin may be present or not but must be loaded before this plugin when it is present.
     *
     * The declared plugin's event handlers will also take priority when it has the same priority as this plugin's event handlers.
     */
    OPTIONAL_BEFORE,

    /**
     * The declared plugin may be present or not but must be loaded after this plugin when it is present.
     *
     * The declared plugin's event handlers will also have lower priority when it has the same priority as this plugin's event handlers.
     */
    OPTIONAL_AFTER,

    /**
     * This plugin will refuse to work when the declared plugin is present and an informative message will be displayed.
     */
    INCOMPATIBLE,

    /**
     * The plugin will refuse to work when the declared plugin is present in an other file container
     * and an informative message will be displayed.
     */
    INCLUDED
}