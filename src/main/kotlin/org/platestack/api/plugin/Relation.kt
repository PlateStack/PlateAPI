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

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.platestack.api.plugin.version.VersionRange

data class Relation(val type: RelationType, val id: String, val namespace: String, val versions: ImmutableList<VersionRange>) {
    constructor(type: RelationType, id: String, namespace: String, versions: Iterable<VersionRange>): this(type, id, namespace, versions.toImmutableList())

    companion object {
        @JvmStatic fun from(annotation: org.platestack.api.plugin.annotation.Relation) =
                Relation(annotation.type, annotation.id.value, annotation.id.namespace, annotation.versions.map { VersionRange.from(it) })
    }

    operator fun contains(metadata: PlateMetadata)
            = namespace == "plate" && id == metadata.id && versions.any { metadata.version in it }
}
