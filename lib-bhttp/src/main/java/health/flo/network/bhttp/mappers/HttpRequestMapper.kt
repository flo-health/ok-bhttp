package health.flo.network.bhttp.mappers

import health.flo.network.bhttp.model.FieldSection.Companion.empty
import health.flo.network.bhttp.model.KnownLengthRequest
import health.flo.network.bhttp.model.plus
import okhttp3.Request

internal class HttpRequestMapper(
    private val requestControlDataMapper: RequestControlDataMapper = RequestControlDataMapper(),
    private val httpHeadersMapper: HttpHeadersMapper = HttpHeadersMapper(),
    private val httpBodyMapper: HttpBodyMapper = HttpBodyMapper(),
    private val httpRequestContentTypeMapper: HttpRequestContentTypeMapper = HttpRequestContentTypeMapper(),
) {

    fun map(request: Request): KnownLengthRequest {
        val content: ByteArray = request.body?.let(httpBodyMapper::map) ?: byteArrayOf()
        val contentTypeHeader = request.body?.contentType()
            ?.let(httpRequestContentTypeMapper::map)
        // The body's content type replaces the header, as OkHttp does on a direct call.
        val headers = when (contentTypeHeader) {
            null -> request.headers
            else -> request.headers.newBuilder().removeAll(contentTypeHeader.name).build()
        }

        return KnownLengthRequest(
            requestControlData = requestControlDataMapper.map(request),
            headerSection = httpHeadersMapper.map(headers) + listOfNotNull(contentTypeHeader),
            content = content,
            trailerSection = empty(),
        )
    }
}
