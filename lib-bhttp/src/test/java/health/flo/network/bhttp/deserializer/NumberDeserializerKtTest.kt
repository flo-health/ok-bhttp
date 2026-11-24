package health.flo.network.bhttp.deserializer

import health.flo.network.bhttp.IntOverflowException
import health.flo.network.bhttp.parseHexEncodedByteArray
import health.flo.test.extensions.toDynamicTests
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class NumberDeserializerKtTest {

    @TestFactory
    fun `variableLengthEncoded should return number decoded number`() = listOf(
        NumberTestData(0.toByte(), "00"),
        NumberTestData(9.toByte(), "09"),
        NumberTestData(29.toByte(), "1d"),
        NumberTestData(63.toByte(), "3f"),
        NumberTestData(64.toShort(), "4040"),
        NumberTestData(15293.toShort(), "7bbd"),
        NumberTestData(16383.toShort(), "7fff"),
        NumberTestData(16384, "80004000"),
        NumberTestData(494878333, "9d7f3e7d"),
        NumberTestData(1073741823, "bfffffff"),
        NumberTestData(1073741824L, "c000000040000000"),
        NumberTestData(151288809941952652L, "c2197c5eff14e88c"),
        NumberTestData(4611686018427387903L, "ffffffffffffffff"),
    ).toDynamicTests { (number, encoded) ->
        val reader = ByteArrayReader.fromByteArray(encoded.parseHexEncodedByteArray())

        val actual = reader.readNumber()

        assertThat(actual).isEqualTo(number)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "c000000080000000", // 2147483648
            "c000000100000005", // 4294967301
        ],
    )
    fun `readInt should throw IntOverflowException when number does not fit in Int`(encoded: String) {
        val reader = ByteArrayReader.fromByteArray(encoded.parseHexEncodedByteArray())

        assertThrows<IntOverflowException> {
            reader.readInt()
        }
    }

    private data class NumberTestData(
        val number: Number,
        val encoded: String,
    )
}
