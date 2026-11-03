package health.flo.network.bhttp.deserializer

import health.flo.network.bhttp.model.FieldSection
import health.flo.network.bhttp.model.KnownLengthInformationalResponse
import health.flo.network.bhttp.model.KnownLengthResponse
import health.flo.network.bhttp.model.ResponseControlData
import health.flo.network.bhttp.model.isInformational

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
        // RFC 9292 section 3.8: an encoder may omit empty trailers, and empty content when the
        // trailers are also empty. Decoders must treat a missing part as a zero length.
        content = if (canRead) readContent() else ByteArray(0),
        trailerSection = if (canRead) readFieldSection() else FieldSection(emptyList()),
    )
}
