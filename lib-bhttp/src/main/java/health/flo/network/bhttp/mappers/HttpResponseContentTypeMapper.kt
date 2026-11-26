package health.flo.network.bhttp.mappers

import health.flo.network.bhttp.model.FieldSection
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull

private const val HEADER_CONTENT_TYPE = "Content-Type"

internal class HttpResponseContentTypeMapper {

    fun map(headers: FieldSection): MediaType? {
        // OkHttp's Headers.get returns the last value, so a direct call uses the last Content-Type.
        return headers.fieldLines.lastOrNull { header -> header.name.equals(HEADER_CONTENT_TYPE, ignoreCase = true) }
            ?.value
            ?.toMediaTypeOrNull()
    }
}
