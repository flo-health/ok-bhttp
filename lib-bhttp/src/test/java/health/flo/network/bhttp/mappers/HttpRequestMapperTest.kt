package health.flo.network.bhttp.mappers

import health.flo.network.bhttp.model.FieldLine
import health.flo.network.bhttp.model.RequestControlData
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

private const val TEST_URL = "https://example.com/a?b=c"
private const val TEST_BODY = "hi"
private const val TEST_MEDIA_TYPE = "text/plain"

internal class HttpRequestMapperTest {

    private val sut = HttpRequestMapper()

    @Test
    fun `map should map request without body`() {
        val request = Request.Builder()
            .url(TEST_URL)
            .header("a", "b")
            .build()
        val expectedControlData = RequestControlData(
            method = "GET",
            scheme = "https",
            authority = "example.com:443",
            path = "/a?b=c",
        )

        val actual = sut.map(request)

        assertThat(actual.requestControlData).isEqualTo(expectedControlData)
        assertThat(actual.headerSection.fieldLines).containsExactly(FieldLine("a", "b"))
        assertThat(actual.content).isEmpty()
        assertThat(actual.trailerSection.fieldLines).isEmpty()
    }

    @Test
    fun `map should add content type header after request headers`() {
        val request = Request.Builder()
            .url(TEST_URL)
            .header("a", "b")
            .post(TEST_BODY.toByteArray().toRequestBody(TEST_MEDIA_TYPE.toMediaType()))
            .build()

        val actual = sut.map(request)

        assertThat(actual.requestControlData.method).isEqualTo("POST")
        assertThat(actual.headerSection.fieldLines).containsExactly(
            FieldLine("a", "b"),
            FieldLine("Content-Type", TEST_MEDIA_TYPE),
        )
        assertThat(String(actual.content)).isEqualTo(TEST_BODY)
    }

    @Test
    fun `map should not add content type header when body has no content type`() {
        val request = Request.Builder()
            .url(TEST_URL)
            .post(TEST_BODY.toByteArray().toRequestBody())
            .build()

        val actual = sut.map(request)

        assertThat(actual.headerSection.fieldLines).isEmpty()
        assertThat(String(actual.content)).isEqualTo(TEST_BODY)
    }

    @Test
    fun `map should keep order of repeated headers`() {
        val request = Request.Builder()
            .url(TEST_URL)
            .addHeader("a", "1")
            .addHeader("b", "2")
            .addHeader("a", "3")
            .build()

        val actual = sut.map(request)

        assertThat(actual.headerSection.fieldLines).containsExactly(
            FieldLine("a", "1"),
            FieldLine("b", "2"),
            FieldLine("a", "3"),
        )
    }
}
