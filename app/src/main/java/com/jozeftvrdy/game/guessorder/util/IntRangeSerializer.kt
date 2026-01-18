package com.jozeftvrdy.game.guessorder.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object IntRangeSerializer : KSerializer<IntRange> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("IntRange") {
        element<Int>("first")
        element<Int>("last")
    }

    override fun serialize(encoder: Encoder, value: IntRange) {
        encoder.encodeStructure(descriptor) {
            encodeIntElement(descriptor, 0, value.first)
            encodeIntElement(descriptor, 1, value.last)
        }
    }

    override fun deserialize(decoder: Decoder): IntRange {
        return decoder.decodeStructure(descriptor) {
            var first = 0
            var last = 0
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> first = decodeIntElement(descriptor, 0)
                    1 -> last = decodeIntElement(descriptor, 1)
                    -1 -> break
                    else -> throw Exception("Unknown index $index")
                }
            }
            first..last
        }
    }
}