package health.flo.network.bhttp.serializer

import health.flo.network.bhttp.UnencodableCharacterException
import health.flo.network.bhttp.hexEncodedString
import health.flo.test.extensions.toDynamicTests
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class StringSerializerKtTest {

    @TestFactory
    fun `binaryEncodedWithLengthPrefix should return encoded string with length prefix`() = listOf(
        StringTestData("https", "05" + "6874747073"),
        StringTestData("www.example.com", "0f" + "7777772e6578616d706c652e636f6d"),
        StringTestData("", "00"),
        StringTestData("café", "05" + "636166c3a9"),
    ).toDynamicTests { (string, encoded) ->
        val actual = string.binaryEncodedWithLengthPrefix()

        assertThat(actual.hexEncodedString()).isEqualTo(encoded)
    }

    @Test
    fun `binaryEncodedWithLengthPrefix should throw UnencodableCharacterException for a lone surrogate`() {
        assertThrows<UnencodableCharacterException> {
            "a\uD800".binaryEncodedWithLengthPrefix()
        }
    }

    private data class StringTestData(
        val string: String,
        val encoded: String,
    )
}
