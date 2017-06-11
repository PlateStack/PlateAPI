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
@file:JvmName("KotlinExtensions")

package org.platestack.util

import kotlin.reflect.KClass

inline fun <R> tryOrNull(task: () -> R): R? {
    try {
        return task()
    }
    catch (caught: Exception) {
        return null
    }
}

inline fun <R> tryIgnoring(first: KClass<out Throwable>, task: () -> R): R? {
    try {
        return task()
    }
    catch (caught: Throwable) {
        if(first.isInstance(caught))
            return null
        throw caught
    }
}

inline fun <R> tryIgnoring(first: KClass<out Throwable>, vararg others: KClass<out Throwable>, task: () -> R): R? {
    try {
        return task()
    }
    catch (caught: Throwable) {
        if(first.isInstance(caught) || others.any { it.isInstance(caught) })
            return null
        throw caught
    }
}

inline fun <F, R> F.letTryIgnoring(first: KClass<out Throwable>, task: (F) -> R): R? {
    return tryIgnoring(first) {
        task(this)
    }
}

inline fun <F, R> F.letTryIgnoring(first: KClass<out Throwable>, vararg others: KClass<out Throwable>, task: (F) -> R): R? {
    return tryIgnoring(first, *others) {
        task(this)
    }
}

inline fun <F, R> F.letTry(task: (F) -> R): R? {
    return tryOrNull {
        task(this)
    }
}

inline fun <F, R> F.tryApplyIgnoring(first: KClass<out Throwable>, task: F.() -> R): R? {
    return tryIgnoring(first) {
        task()
    }
}

inline fun <F, R> F.tryApplyIgnoring(first: KClass<out Throwable>, vararg others: KClass<out Throwable>, task: F.() -> R): R? {
    return tryIgnoring(first, *others) {
        task()
    }
}

inline fun <F, R> F.tryApply(task: F.() -> R): R? {
    return tryOrNull {
        task()
    }
}
