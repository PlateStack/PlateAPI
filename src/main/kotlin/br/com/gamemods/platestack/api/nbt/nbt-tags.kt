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

package br.com.gamemods.platestack.api.nbt

import br.com.gamemods.platestack.api.json.JsonText
import br.com.gamemods.platestack.api.message.Text
import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.toJson
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableHashMap
import kotlinx.collections.immutable.toImmutableList
import java.util.*

interface DataTag {
    val value: Any

    fun toMutable(): MutableTag
    fun toImmutable(): ImmutableTag
    fun toJson(): JsonElement

    interface ByteTag: DataTag {
        val byte: Byte
        override val value: Byte get() = byte
        override fun toMutable() = MutableTag.ByteTag(byte)
        override fun toImmutable() = ImmutableTag.ByteTag(byte)
        override fun toJson() = byte.toJson()
    }

    interface ShortTag: DataTag {
        val short: Short
        override val value: Short get() =  short
        override fun toMutable() = MutableTag.ShortTag(short)
        override fun toImmutable() = ImmutableTag.ShortTag(short)
        override fun toJson() = short.toJson()
    }

    interface IntTag: DataTag {
        val int: Int
        override val value: Int get() = int
        override fun toMutable() = MutableTag.IntTag(int)
        override fun toImmutable() = ImmutableTag.IntTag(int)
        override fun toJson() = int.toJson()
    }

    interface LongTag: DataTag {
        val long: Long
        override val value: Long get() = long
        override fun toMutable() = MutableTag.LongTag(long)
        override fun toImmutable() = ImmutableTag.LongTag(long)
        override fun toJson() = long.toJson()
    }

    interface FloatTag: DataTag {
        val float: Float
        override val value: Any get() = float
        override fun toMutable() = MutableTag.FloatTag(float)
        override fun toImmutable() = ImmutableTag.FloatTag(float)
        override fun toJson() = float.toJson()
    }

    interface DoubleTag: DataTag {
        val double: Double
        override val value: Any get() = double
        override fun toMutable() = MutableTag.DoubleTag(double)
        override fun toImmutable() = ImmutableTag.DoubleTag(double)
        override fun toJson() = double.toJson()
    }

    interface ByteArrayTag: DataTag {
        val bytes: ByteArray
        override val value: ByteArray get() = bytes
        override fun toMutable() = MutableTag.ByteArrayTag(bytes)
        override fun toImmutable() = ImmutableTag.ByteArrayTag(bytes)
        override fun toJson() = JsonPrimitive("bytearray:"+Base64.getEncoder().encodeToString(bytes))
    }

    interface StringTag: DataTag {
        val string: String
        override val value: String get() = string
        override fun toMutable() = MutableTag.StringTag(string)
        override fun toImmutable() = ImmutableTag.StringTag(string)
        override fun toJson() = value.toJson()
    }

    interface ListTag<T: DataTag>: DataTag, List<T> {
        val type: Class<T>
        val list: List<T>
        override val value: Any get() = list
        override fun toMutable() = MutableTag.ListTag(type.toMutable(), list.mapTo(mutableListOf(), DataTag::toMutable))
        override fun toImmutable() = ImmutableTag.ListTag(type.toImmutable(), iterable = list.mapTo(mutableListOf(), DataTag::toImmutable))
        override fun toJson() = jsonArray(list.map(DataTag::toJson))
        companion object {
            fun Class<out DataTag>.toMutable(): Class<out MutableTag> {
                if(isAssignableFrom(MutableTag::class.java))
                    return this.asSubclass(MutableTag::class.java)
                else if(isAssignableFrom(ImmutableTag::class.java))
                    return when(this) {
                        ImmutableTag.ByteTag::class.java -> MutableTag.ByteTag::class.java
                        ImmutableTag.ShortTag::class.java -> MutableTag.ByteTag::class.java
                        ImmutableTag.IntTag::class.java -> MutableTag.ByteTag::class.java
                        ImmutableTag.LongTag::class.java -> MutableTag.ByteTag::class.java
                        ImmutableTag.FloatTag::class.java -> MutableTag.ByteTag::class.java
                        ImmutableTag.DoubleTag::class.java -> MutableTag.ByteTag::class.java
                        ImmutableTag.ByteArrayTag::class.java -> MutableTag.ByteTag::class.java
                        ImmutableTag.StringTag::class.java -> MutableTag.ByteTag::class.java
                        ImmutableTag.ListTag::class.java -> MutableTag.ByteTag::class.java
                        ImmutableTag.CompoundTag::class.java -> MutableTag.ByteTag::class.java
                        ImmutableTag.IntArrayTag::class.java -> MutableTag.ByteTag::class.java
                        else -> throw UnsupportedOperationException("The mutable class for $this is unknown")
                    }
                else
                    error("Unexpected class $this")
            }
        }

        fun Class<out DataTag>.toImmutable(): Class<out ImmutableTag> {
            if(isAssignableFrom(ImmutableTag::class.java))
                return this.asSubclass(ImmutableTag::class.java)
            else if(isAssignableFrom(MutableTag::class.java))
                return when(this) {
                    MutableTag.ByteTag::class.java -> ImmutableTag.ByteTag::class.java
                    MutableTag.ShortTag::class.java -> ImmutableTag.ByteTag::class.java
                    MutableTag.IntTag::class.java -> ImmutableTag.ByteTag::class.java
                    MutableTag.LongTag::class.java -> ImmutableTag.ByteTag::class.java
                    MutableTag.FloatTag::class.java -> ImmutableTag.ByteTag::class.java
                    MutableTag.DoubleTag::class.java -> ImmutableTag.ByteTag::class.java
                    MutableTag.ByteArrayTag::class.java -> ImmutableTag.ByteTag::class.java
                    MutableTag.StringTag::class.java -> ImmutableTag.ByteTag::class.java
                    MutableTag.ListTag::class.java -> ImmutableTag.ByteTag::class.java
                    MutableTag.CompoundTag::class.java -> ImmutableTag.ByteTag::class.java
                    MutableTag.IntArrayTag::class.java -> ImmutableTag.ByteTag::class.java
                    else -> throw UnsupportedOperationException("The immutable class for $this is unknown")
                }
            else
                error("Unexpected class $this")
        }
    }

    interface CompoundTag<out T: DataTag>: DataTag, Map<String, T> {
        val compound: Map<String, T>
        override val value: Any get() = compound
        override fun toMutable() = MutableTag.CompoundTag(compound.mapValuesTo(mutableMapOf(),{ it.value.toMutable() }))
        override fun toImmutable() = ImmutableTag.CompoundTag(compound.mapValues { it.value.toImmutable() }.toImmutableHashMap())
        override fun toJson() = jsonObject(compound.map { it.key to it.value.toJson() })
    }

    interface IntArrayTag: DataTag, List<Int> {
        val array: IntArray
        override val value: Any get() = array
        override fun toMutable() = MutableTag.IntArrayTag(array.clone())
        override fun toImmutable() = ImmutableTag.IntArrayTag(array.clone())
        override fun toJson() = jsonArray(array)
    }

    // Extra tags

    interface TextTag: DataTag {
        val text: Text
        override val value: Text get() = text
        override fun toMutable() = MutableTag.TextTag(text)
        override fun toImmutable() = ImmutableTag.TextTag(text)
        override fun toJson() = JsonText(text)
    }

    interface JsonTag: DataTag {
        val json: JsonElement
        override val value: JsonElement get() = json
        override fun toMutable() = MutableTag.JsonTag(json)
        override fun toImmutable() = ImmutableTag.JsonTag(json)
        override fun toJson() = value.toString().toJson()
    }
}

abstract class AbstractTag internal constructor(): DataTag {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as MutableTag

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

sealed class MutableTag: AbstractTag() {
    class ByteTag(override var byte: Byte) : MutableTag(), DataTag.ByteTag
    class ShortTag(override var short: Short) : MutableTag(), DataTag.ShortTag
    class IntTag(override var int: Int) : MutableTag(), DataTag.IntTag
    class LongTag(override var long: Long) : MutableTag(), DataTag.LongTag
    class FloatTag(override var float: Float) : MutableTag(), DataTag.FloatTag
    class DoubleTag(override var double: Double) : MutableTag(), DataTag.DoubleTag
    class ByteArrayTag(override var bytes: ByteArray) : MutableTag(), DataTag.ByteArrayTag
    class StringTag(override var string: String) : MutableTag(), DataTag.StringTag
    class ListTag<T: MutableTag>(override val type: Class<T>, override var list: MutableList<T>) : MutableTag(), DataTag.ListTag<T>, MutableList<T> by list
    class CompoundTag(override var compound: MutableMap<String, MutableTag>): MutableTag(), DataTag.CompoundTag<MutableTag>, MutableMap<String, MutableTag> by compound
    class IntArrayTag(override var array: IntArray): MutableTag(), DataTag.IntArrayTag, List<Int> by array.asList()
    class TextTag(override var text: Text) : MutableTag(), DataTag.TextTag
    class JsonTag(override var json: JsonElement) : MutableTag(), DataTag.JsonTag
}

sealed class ImmutableTag: AbstractTag() {
    class ByteTag(override val byte: Byte) : ImmutableTag(), DataTag.ByteTag
    class ShortTag(override val short: Short) : ImmutableTag(), DataTag.ShortTag
    class IntTag(override val int: Int) : ImmutableTag(), DataTag.IntTag
    class LongTag(override val long: Long) : ImmutableTag(), DataTag.LongTag
    class FloatTag(override val float: Float) : ImmutableTag(), DataTag.FloatTag
    class DoubleTag(override val double: Double) : ImmutableTag(), DataTag.DoubleTag
    class ByteArrayTag(override val bytes: ByteArray) : ImmutableTag(), DataTag.ByteArrayTag
    class StringTag(override val string: String) : ImmutableTag(), DataTag.StringTag
    class ListTag<T: ImmutableTag>(override val type: Class<T>, override val list: ImmutableList<T>) : ImmutableTag(), DataTag.ListTag<T>, List<T> by list {
        constructor(type: Class<T>, iterable: Iterable<T>): this(type, iterable.toImmutableList())
    }
    class CompoundTag(override val compound: ImmutableMap<String, ImmutableTag>): ImmutableTag(), DataTag.CompoundTag<ImmutableTag>, Map<String, ImmutableTag> by compound
    class IntArrayTag(override val array: IntArray): ImmutableTag(), DataTag.IntArrayTag, List<Int> by array.asList()
    class TextTag(override val text: Text) : ImmutableTag(), DataTag.TextTag
    class JsonTag(override val json: JsonElement) : ImmutableTag(), DataTag.JsonTag
}
