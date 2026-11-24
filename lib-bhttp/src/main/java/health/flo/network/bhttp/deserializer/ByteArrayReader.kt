package health.flo.network.bhttp.deserializer

import health.flo.network.bhttp.EndOfArrayException

internal interface ByteArrayReader {

    val pointer: Int
    val size: Int
    val canRead: Boolean
    val firstByte: Byte
    val firstByteOrNull: Byte?

    @Throws(EndOfArrayException::class)
    fun read(count: Int): ByteArray

    companion object {

        fun fromByteArray(array: ByteArray): ByteArrayReader = ByteArrayReaderImpl(array)
    }
}

internal fun ByteArrayReader.slice(sliceSize: Int): ByteArrayReader = SliceByteArrayReader(this, sliceSize)

abstract class ByteArrayReaderBase : ByteArrayReader {

    protected fun throwWhenEndReached(offset: Int = 1) {
        if (!fits(offset)) {
            throw EndOfArrayException()
        }
    }
}

internal fun ByteArrayReader.fits(count: Int): Boolean = count in 0..(size - pointer)

private class ByteArrayReaderImpl(
    private val array: ByteArray,
) : ByteArrayReaderBase() {

    private var _pointer = 0

    override val size: Int
        get() = array.size

    override val pointer: Int
        get() = _pointer

    override val canRead: Boolean
        get() = pointer < size

    override val firstByte: Byte
        get() = firstByteOrNull ?: throw EndOfArrayException()

    override val firstByteOrNull: Byte?
        get() = array.getOrNull(pointer)

    override fun read(count: Int): ByteArray {
        throwWhenEndReached(count)

        return array.sliceArray(pointer until pointer + count)
            .also {
                _pointer += count
            }
    }
}

private class SliceByteArrayReader(
    private val reader: ByteArrayReader,
    private val sliceSize: Int,
) : ByteArrayReaderBase() {

    private val startIndex = reader.pointer

    override val pointer: Int
        get() = reader.pointer - startIndex

    override val size: Int
        get() = sliceSize

    override val canRead: Boolean
        get() = reader.canRead && pointer < size

    override val firstByte: Byte
        get() {
            throwWhenEndReached()

            return reader.firstByte
        }

    override val firstByteOrNull: Byte?
        get() = when (canRead) {
            true -> reader.firstByteOrNull
            false -> null
        }

    init {
        if (!reader.fits(sliceSize)) {
            throw EndOfArrayException()
        }
    }

    override fun read(count: Int): ByteArray {
        throwWhenEndReached(count)

        return reader.read(count)
    }
}
