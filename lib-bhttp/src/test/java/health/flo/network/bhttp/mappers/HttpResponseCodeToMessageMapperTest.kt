package health.flo.network.bhttp.mappers

import health.flo.test.extensions.toDynamicTests
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestFactory

internal class HttpResponseCodeToMessageMapperTest {

    private val sut = HttpResponseCodeToMessageMapper()

    @TestFactory
    fun `map should return message for status code`() = listOf(
        TestData(100, "CONTINUE"),
        TestData(200, "OK"),
        TestData(204, "NO CONTENT"),
        TestData(304, "NOT MODIFIED"),
        TestData(404, "NOT FOUND"),
        TestData(500, "INTERNAL SERVER ERROR"),
        TestData(299, "UNKNOWN #299"),
    ).toDynamicTests { (code, message) ->
        val actual = sut.map(code)

        assertThat(actual).isEqualTo(message)
    }

    private data class TestData(
        val code: Int,
        val message: String,
    )
}
