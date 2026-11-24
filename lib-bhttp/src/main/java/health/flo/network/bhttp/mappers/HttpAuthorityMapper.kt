package health.flo.network.bhttp.mappers

import okhttp3.HttpUrl

internal class HttpAuthorityMapper {

    fun map(httpUrl: HttpUrl): String {
        // HttpUrl.host drops the brackets of an IPv6 address.
        val host = when (':' in httpUrl.host) {
            true -> "[${httpUrl.host}]"
            false -> httpUrl.host
        }

        return "$host:${httpUrl.port}"
    }
}
