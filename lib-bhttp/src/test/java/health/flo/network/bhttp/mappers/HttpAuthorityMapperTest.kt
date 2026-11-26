package health.flo.network.bhttp.mappers

import health.flo.test.extensions.toDynamicTests
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestFactory

private const val TEST_HTTP_URL = "http://www.google.com/search?q=polar%20bears"
private const val TEST_HTTP_AUTHORITY = "www.google.com:80"

private const val TEST_HTTPS_URL = "https://www.google.com/search?q=polar%20bears"
private const val TEST_HTTPS_AUTHORITY = "www.google.com:443"

private const val TEST_URL_WITH_PORT = "https://www.google.com:82/search?q=polar%20bears"
private const val TEST_AUTHORITY_WITH_PORT = "www.google.com:82"

private const val TEST_IP_WITH_PORT = "http://127.0.0.1:99/search?q=polar%20bears"
private const val TEST_IP_WITH_PORT_AUTHORITY = "127.0.0.1:99"

private const val TEST_IPV6_WITH_PORT = "https://[2001:db8::1]:8443/search?q=polar%20bears"
private const val TEST_IPV6_WITH_PORT_AUTHORITY = "[2001:db8::1]:8443"

internal class HttpAuthorityMapperTest {

    private val sut = HttpAuthorityMapper()

    @TestFactory
    fun `map should return correct path`() = listOf(
        TestData(TEST_HTTP_URL, TEST_HTTP_AUTHORITY),
        TestData(TEST_HTTPS_URL, TEST_HTTPS_AUTHORITY),
        TestData(TEST_URL_WITH_PORT, TEST_AUTHORITY_WITH_PORT),
        TestData(TEST_IP_WITH_PORT, TEST_IP_WITH_PORT_AUTHORITY),
        TestData(TEST_IPV6_WITH_PORT, TEST_IPV6_WITH_PORT_AUTHORITY),
    ).toDynamicTests { (url, authority) ->
        val httpUrl = url.toHttpUrl()

        val actual = sut.map(httpUrl)

        assertThat(actual).isEqualTo(authority)
    }

    private data class TestData(
        val url: String,
        val authority: String,
    )
}
