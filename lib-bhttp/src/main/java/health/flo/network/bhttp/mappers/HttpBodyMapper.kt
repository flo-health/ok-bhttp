package health.flo.network.bhttp.mappers

import health.flo.network.bhttp.DuplexBodyException
import okhttp3.RequestBody
import okio.Buffer

internal class HttpBodyMapper {

    fun map(body: RequestBody): ByteArray {
        // A duplex body writes to the sink after writeTo returns, so the buffer would miss it.
        if (body.isDuplex()) {
            throw DuplexBodyException()
        }

        val buffer = Buffer()
        body.writeTo(buffer)

        return buffer.readByteArray()
    }
}
