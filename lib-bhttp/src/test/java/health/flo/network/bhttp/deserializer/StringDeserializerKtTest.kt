package health.flo.network.bhttp.deserializer

import health.flo.network.bhttp.parseHexEncodedByteArray
import health.flo.test.extensions.toDynamicTests
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestFactory

internal class StringDeserializerKtTest {

    @TestFactory
    fun readString() = listOf(
        StringTestData("https", "05" + "6874747073"),
        StringTestData("www.example.com", "0f" + "7777772e6578616d706c652e636f6d"),
        StringTestData("", "00"),
        StringTestData("café", "05" + "636166c3a9"),
    ).toDynamicTests { (expected, encoded) ->
        val reader = ByteArrayReader.fromByteArray(encoded.parseHexEncodedByteArray())

        val actual = reader.readString()

        assertThat(actual).isEqualTo(expected)
    }

    private data class StringTestData(
        val string: String,
        val encoded: String,
    )
}
