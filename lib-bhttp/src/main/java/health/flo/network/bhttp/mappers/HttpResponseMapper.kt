package health.flo.network.bhttp.mappers

import health.flo.network.bhttp.InvalidHeaderException
import health.flo.network.bhttp.model.KnownLengthResponse
import okhttp3.Headers
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

private const val FORBIDDEN_VALUE_CHARS = "\r\n\u0000"

internal class HttpResponseMapper(
    private val httpResponseCodeToMessageMapper: HttpResponseCodeToMessageMapper = HttpResponseCodeToMessageMapper(),
    private val httpResponseContentTypeMapper: HttpResponseContentTypeMapper = HttpResponseContentTypeMapper(),
) {

    fun map(knownLengthResponse: KnownLengthResponse, protocol: Protocol, request: Request): Response {
        val headers = Headers.Builder()
        knownLengthResponse.headerSection.fieldLines.forEach { header ->
            // RFC 9110 5.5: CR, LF and NUL in a field value are invalid; addUnsafeNonAscii checks only the name.
            if (header.value.any { char -> char in FORBIDDEN_VALUE_CHARS }) {
                throw InvalidHeaderException()
            }
            try {
                headers.addUnsafeNonAscii(header.name, header.value)
            } catch (exception: IllegalArgumentException) {
                throw InvalidHeaderException(exception)
            }
        }

        val builder = Response.Builder()
            .code(knownLengthResponse.controlData.statusCode)
            .message(httpResponseCodeToMessageMapper.map(knownLengthResponse.controlData.statusCode))
            .protocol(protocol)
            .request(request)
            .headers(headers.build())

        val contentType = httpResponseContentTypeMapper.map(knownLengthResponse.headerSection)
        val body = knownLengthResponse.content.toResponseBody(contentType)
        builder.body(body)

        return builder.build()
    }
}
