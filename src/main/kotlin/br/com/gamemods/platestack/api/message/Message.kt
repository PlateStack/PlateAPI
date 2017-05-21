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

package br.com.gamemods.platestack.api.message

import br.com.gamemods.platestack.api.structure.remap
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.immutableMapOf
import kotlinx.collections.immutable.toImmutableHashMap
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger

data class Message(val sentence: Sentence, val parameterValues: ImmutableMap<String, ParameterValue>): Serializable {
    constructor(sentence: Sentence, parameters: Map<String, Serializable>)
            : this(sentence, parameters.mapValues { ParameterValue.create(it.value) }.toImmutableHashMap())

    constructor(rawText: String): this(Sentence(rawText), immutableMapOf())

    val parameters: Map<String, Serializable> = parameterValues.remap { it.value.value }

    fun copy(sentence: Sentence = this.sentence, parameters: Map<String, Serializable> = this.parameters) = Message(sentence, parameters)

    sealed class ParameterValue: Serializable {
        companion object {
            @JvmStatic fun create(obj: Serializable) = when(obj) {
                // Common
                is ParameterValue -> obj
                is Message -> MessageValue(obj)
                is Text -> TextValue(obj)
                is Int -> IntValue(obj)
                is Long -> LongValue(obj)
                is Float -> FloatValue(obj)
                is Double -> DoubleValue(obj)

                // Not Common
                is ChatColor -> ChatColorValue(obj)
                is ChatFormat -> ChatFormatValue(obj)
                is Style -> StyleValue(obj)
                is Byte -> ByteValue(obj)
                is Short -> ShortValue(obj)
                is Char -> CharValue(obj)
                is BigDecimal -> BigDecimalValue(obj)
                is BigInteger -> BigIntegerValue(obj)
                else -> TODO("Unable to store $obj as a parameter value")
            }
        }

        abstract val value: Serializable

        class BigDecimalValue(override val value: BigDecimal): ParameterValue()
        class BigIntegerValue(override val value: BigInteger): ParameterValue()
        class MessageValue(override val value: Message): ParameterValue()
        class TextValue(override val value: Text): ParameterValue()
        class ChatColorValue(override val value: ChatColor): ParameterValue()
        class ChatFormatValue(override val value: ChatFormat): ParameterValue()
        class StyleValue(override val value: Style): ParameterValue()

        class ByteValue(val byte: Byte): ParameterValue() {
            override val value: Byte get() = byte
        }

        class ShortValue(val short: Short): ParameterValue() {
            override val value: Short get() = short
        }

        class CharValue(val char: Char): ParameterValue() {
            override val value: Char get() = char
        }

        class IntValue(val int: Int): ParameterValue() {
            override val value: Int get() = int
        }

        class LongValue(val long: Long): ParameterValue() {
            override val value: Long get() = long
        }

        class FloatValue(val float: Float): ParameterValue() {
            override val value: Float get() = float
        }

        class DoubleValue(val double: Double): ParameterValue() {
            override val value: Double get() = double
        }
    }
}
