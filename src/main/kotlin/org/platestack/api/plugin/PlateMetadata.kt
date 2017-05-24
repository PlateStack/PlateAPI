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

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import org.platestack.api.plugin.annotation.Plate
import org.platestack.api.plugin.version.Version

data class PlateMetadata(val id: String, val name: String, val version: Version, val relations: ImmutableSet<Relation>) {
    constructor(id: String, name: String, version: Version, relations: Iterable<Relation>): this(id, name, version, relations.toImmutableSet())
    constructor(annotation: Plate): this(annotation.id, annotation.name, Version.from(annotation.version), annotation.relations.map { Relation.from(it) })
}
