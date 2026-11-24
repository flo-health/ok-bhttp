package health.flo.network.bhttp.mappers

import health.flo.network.bhttp.BodyDataFactory.TEST_BODY
import health.flo.network.bhttp.DuplexBodyException
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private const val TEST_MEDIA_TYPE = "application/json"

internal class HttpBodyMapperTest {

    private val sut = HttpBodyMapper()

    @Test
    fun `map should return byte array with the request's body`() {
        val mediaType = TEST_MEDIA_TYPE.toMediaType()
        val body = TEST_BODY.toRequestBody(mediaType)

        val actual = sut.map(body)

        assertThat(String(actual)).isEqualTo(TEST_BODY)
    }

    @Test
    fun `map should return byte array with the body of unknown length`() {
        val body = object : RequestBody() {
            override fun contentType(): MediaType = TEST_MEDIA_TYPE.toMediaType()

            override fun contentLength(): Long = -1

            override fun writeTo(sink: BufferedSink) {
                sink.writeUtf8(TEST_BODY)
            }
        }

        val actual = sut.map(body)

        assertThat(String(actual)).isEqualTo(TEST_BODY)
    }

    @Test
    fun `map should throw DuplexBodyException for a duplex body`() {
        val body = object : RequestBody() {
            override fun contentType(): MediaType = TEST_MEDIA_TYPE.toMediaType()

            override fun isDuplex(): Boolean = true

            override fun writeTo(sink: BufferedSink) = Unit
        }

        assertThrows<DuplexBodyException> {
            sut.map(body)
        }
    }
}
