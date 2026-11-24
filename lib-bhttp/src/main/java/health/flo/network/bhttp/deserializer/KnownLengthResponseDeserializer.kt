package health.flo.network.bhttp.deserializer

import health.flo.network.bhttp.model.FieldSection
import health.flo.network.bhttp.model.KnownLengthInformationalResponse
import health.flo.network.bhttp.model.KnownLengthResponse
import health.flo.network.bhttp.model.ResponseControlData
import health.flo.network.bhttp.model.isInformational
import health.flo.network.bhttp.utils.emptyByteArray

internal fun ByteArrayReader.readKnownLengthResponse(): KnownLengthResponse {
    var responseControlData: ResponseControlData = readResponseControlData()
    val informationalResponses = buildList<KnownLengthInformationalResponse> {
        while (responseControlData.isInformational) {
            KnownLengthInformationalResponse(
                controlData = responseControlData,
                fieldSection = readFieldSection(),
            ).let(::add)
            responseControlData = readResponseControlData()
        }
    }

    return KnownLengthResponse(
        informationalResponses = informationalResponses,
        controlData = responseControlData,
        headerSection = readFieldSection(),
        // RFC 9292 3.8: omitted content and trailers are equivalent to zero length.
        content = if (canRead) readContent() else emptyByteArray(),
        trailerSection = if (canRead) readFieldSection() else FieldSection.empty(),
    )
}
