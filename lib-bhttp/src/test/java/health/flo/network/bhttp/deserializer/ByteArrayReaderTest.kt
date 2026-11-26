package health.flo.network.bhttp.deserializer

import health.flo.network.bhttp.EndOfArrayException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

private const val ARRAY_SIZE = 10

internal class ByteArrayReaderImplTest : ByteArrayReaderTest() {

    private val array = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)

    override val sut = ByteArrayReader.fromByteArray(array)
}

internal class SliceByteArrayReaderTest : ByteArrayReaderTest() {

    private val array = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0)

    override val sut = ByteArrayReader.fromByteArray(array)
        .apply { read(1) }
        .slice(ARRAY_SIZE)

    @Test
    fun `slice should throw EndOfArrayException when slice is longer than remaining bytes`() {
        val parent = ByteArrayReader.fromByteArray(byteArrayOf(1, 2, 3)).apply { read(1) }

        assertThrows<EndOfArrayException> {
            parent.slice(3)
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, Int.MAX_VALUE])
    fun `slice should throw EndOfArrayException when slice size is negative or overflows`(sliceSize: Int) {
        val parent = ByteArrayReader.fromByteArray(byteArrayOf(1, 2, 3)).apply { read(1) }

        assertThrows<EndOfArrayException> {
            parent.slice(sliceSize)
        }
    }

    @Test
    fun `read should not read past slice end when parent has more bytes`() {
        val parent = ByteArrayReader.fromByteArray(byteArrayOf(1, 2, 3, 4))
        val slice = parent.slice(2)

        slice.read(2)

        assertThat(slice.canRead).isFalse()
        assertThat(slice.firstByteOrNull).isNull()
        assertThat(parent.canRead).isTrue()
        assertThrows<EndOfArrayException> {
            slice.read(1)
        }
    }
}

internal abstract class ByteArrayReaderTest {

    protected abstract val sut: ByteArrayReader

    @Test
    fun `size should return correct size`() {
        val expected = ARRAY_SIZE

        assertThat(sut.size).isEqualTo(expected)
    }

    @Test
    fun `pointer should be at 0 position after creation`() {
        val expected = 0

        assertThat(sut.pointer).isEqualTo(expected)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 4, 10])
    fun `pointer should shift after reading`(bytesToRead: Int) {
        val expected = bytesToRead

        sut.read(bytesToRead)

        assertThat(sut.pointer).isEqualTo(expected)
    }

    @ParameterizedTest
    @ValueSource(ints = [11, 16])
    fun `read should throw EndOfArrayException on attempt to read too many bytes`(bytesToRead: Int) {
        assertThrows<EndOfArrayException> {
            sut.read(bytesToRead)
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, Int.MAX_VALUE])
    fun `read should throw EndOfArrayException when count is negative or overflows`(bytesToRead: Int) {
        sut.read(1)

        assertThrows<EndOfArrayException> {
            sut.read(bytesToRead)
        }
    }

    @Test
    fun `canRead should return true when pointer is not at the end of array`() {
        assertThat(sut.canRead).isTrue
    }

    @Test
    fun `canRead should return false when pointer is at the end of array`() {
        sut.read(ARRAY_SIZE)

        assertThat(sut.canRead).isFalse
    }

    @Test
    fun `firstByte should return current byte if existing`() {
        val expected: Byte = 1

        assertThat(sut.firstByte).isEqualTo(expected)
    }

    @Test
    fun `firstByte should throw EndOfArrayException when no bytes to read`() {
        sut.read(ARRAY_SIZE)

        assertThrows<EndOfArrayException> {
            sut.firstByte
        }
    }

    @Test
    fun `firstByteOrNull should return current byte if existing`() {
        val expected: Byte = 1

        assertThat(sut.firstByteOrNull).isEqualTo(expected)
    }

    @Test
    fun `firstByteOrNull should return null when no bytes to read`() {
        sut.read(ARRAY_SIZE)

        assertThat(sut.firstByteOrNull).isNull()
    }

    @Test
    fun `read should return the next bytes from array`() {
        val expectedFirst = byteArrayOf(1)
        val expectedSecond = byteArrayOf(2, 3, 4)

        val first = sut.read(expectedFirst.size)
        val second = sut.read(expectedSecond.size)

        assertThat(first).isEqualTo(expectedFirst)
        assertThat(second).isEqualTo(expectedSecond)
    }
}
