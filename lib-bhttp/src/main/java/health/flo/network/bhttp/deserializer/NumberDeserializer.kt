package health.flo.network.bhttp.deserializer

import health.flo.network.bhttp.IntOverflowException
import health.flo.network.bhttp.model.IntegerLength
import health.flo.network.bhttp.model.IntegerLength.BYTE
import health.flo.network.bhttp.model.IntegerLength.INT
import health.flo.network.bhttp.model.IntegerLength.LONG
import health.flo.network.bhttp.model.IntegerLength.SHORT
import java.nio.ByteBuffer
import kotlin.experimental.and

internal fun ByteArrayReader.readNumber(): Number {
    val length = IntegerLength.readLength(this.firstByte)
    val serialized = this.read(length.lengthInBytes).eraseLengthBits()
    val buffer = ByteBuffer.wrap(serialized)

    return when (length) {
        BYTE -> buffer.get()
        SHORT -> buffer.short
        INT -> buffer.int
        LONG -> buffer.long
    }
}

internal fun ByteArrayReader.readInt(): Int {
    val number = readNumber().toLong()
    if (number > Int.MAX_VALUE) {
        throw IntOverflowException(number)
    }

    return number.toInt()
}

private fun ByteArray.eraseLengthBits(): ByteArray {
    this[0] = this[0].eraseLengthBits()

    return this
}

private fun Byte.eraseLengthBits(): Byte = this.and(0b00111111)
