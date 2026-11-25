package health.flo.network.bhttp

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

private const val TEST_URL = "https://example.com/a"

internal class OkHttpBinarySerializerTest {

    private val sut = OkHttpBinarySerializer.create()

    private val request = Request.Builder()
        .url(TEST_URL)
        .header("a", "b")
        .build()

    @Test
    fun `serialize should encode request as known-length message`() {
        val expected = listOf(
            "00", // Framing indicator
            "03", "474554", // Method (GET)
            "05", "6874747073", // Scheme (https)
            "0f", "6578616d706c652e636f6d3a343433", // Authority (example.com:443)
            "02", "2f61", // Path (/a)
            "04", "01", "61", "01", "62", // Header section (a: b)
            "00", // Content length (0)
            "00", // Trailer section length (0)
        ).joinToString(separator = "")

        val actual = sut.serialize(request)

        assertThat(actual.data.hexEncodedString()).isEqualTo(expected)
    }

    @Test
    fun `deserialize should map known-length message to response`() {
        val responseHex = listOf(
            "01", // Framing indicator
            "40c8", // Status code (200)
            "1e", // Header section length (30)
            "0c", "636f6e74656e742d74797065", // Name (content-type)
            "0a", "746578742f706c61696e", // Value (text/plain)
            "03", "782d61", // Name (x-a)
            "01", "62", // Value (b)
            "02", "6869", // Content (hi)
            "00", // Trailer section length (0)
        ).joinToString(separator = "")

        val actual = sut.deserialize(responseBinaryData(responseHex), Protocol.HTTP_2, request)

        assertThat(actual.code).isEqualTo(200)
        assertThat(actual.message).isEqualTo("OK")
        assertThat(actual.protocol).isEqualTo(Protocol.HTTP_2)
        assertThat(actual.request).isSameAs(request)
        assertThat(actual.header("x-a")).isEqualTo("b")
        assertThat(actual.body.contentType()).isEqualTo("text/plain".toMediaType())
        assertThat(actual.body.string()).isEqualTo("hi")
    }

    @Test
    fun `deserialize should skip informational responses`() {
        val responseHex = listOf(
            "01", // Framing indicator
            "4067", // Informational status code (103)
            "0a", // Informational field section length (10)
            "04", "6c696e6b", // Name (link)
            "04", "3c2f613e", // Value (</a>)
            "40c8", // Status code (200)
            "00", // Header section length (0)
            "00", // Content length (0)
            "00", // Trailer section length (0)
        ).joinToString(separator = "")

        val actual = sut.deserialize(responseBinaryData(responseHex), Protocol.HTTP_1_1, request)

        assertThat(actual.code).isEqualTo(200)
        assertThat(actual.header("link")).isNull()
    }

    @ParameterizedTest
    @ValueSource(strings = ["00", "02", "03"])
    fun `deserialize should throw UnknownFramingIndicatorException for unsupported framing`(framingHex: String) {
        assertThrows<UnknownFramingIndicatorException> {
            sut.deserialize(responseBinaryData(framingHex), Protocol.HTTP_1_1, request)
        }
    }

    private fun responseBinaryData(hex: String) = ResponseBinaryData(hex.parseHexEncodedByteArray())
}
