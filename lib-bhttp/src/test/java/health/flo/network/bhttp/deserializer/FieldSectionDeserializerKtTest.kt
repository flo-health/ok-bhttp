package health.flo.network.bhttp.deserializer

import health.flo.network.bhttp.EndOfArrayException
import health.flo.network.bhttp.model.FieldLine
import health.flo.network.bhttp.parseHexEncodedByteArray
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class FieldSectionDeserializerKtTest {

    @Test
    fun `readFieldSection should return empty section for zero length`() {
        val reader = reader("00")

        val actual = reader.readFieldSection()

        assertThat(actual.fieldLines).isEmpty()
        assertThat(reader.pointer).isEqualTo(1)
    }

    @Test
    fun `readFieldSection should read all field lines and stop at section end`() {
        val reader = reader(
            "08", // Section length (8)
            "01", "61", "01", "62", // a: b
            "01", "63", "01", "64", // c: d
            "ff", // Next data
        )

        val actual = reader.readFieldSection()

        assertThat(actual.fieldLines).containsExactly(FieldLine("a", "b"), FieldLine("c", "d"))
        assertThat(reader.pointer).isEqualTo(9)
    }

    @Test
    fun `readFieldSection should throw EndOfArrayException when section is longer than data`() {
        val reader = reader(
            "05", // Section length (5)
            "01", "61", "01", "62", // a: b
        )

        assertThrows<EndOfArrayException> {
            reader.readFieldSection()
        }
    }

    @Test
    fun `readFieldSection should throw EndOfArrayException when section length overflows`() {
        val reader = reader(
            "c00000007fffffff", // Section length (2147483647)
            "01", "61", "01", "62", // a: b
        )

        assertThrows<EndOfArrayException> {
            reader.readFieldSection()
        }
    }

    private fun reader(vararg hex: String) =
        ByteArrayReader.fromByteArray(hex.joinToString(separator = "").parseHexEncodedByteArray())
}
