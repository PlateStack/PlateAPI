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

package org.platestack.util

import kotlin.reflect.KClass

/**
 * Indicates that the annotated element is used by reflection.
 *
 * Adjust your IDE to suppress unused warnings from elements which contains this annotation.
 *
 * @param value The classes which uses the annotated element
 * @param classNames Same as [value] but may be used when the class is not on classpath
 */
@MustBeDocumented @Retention(AnnotationRetention.BINARY)
annotation class ReflectionTarget(vararg val value: KClass<*> = emptyArray(), val classNames: Array<String> = emptyArray())
