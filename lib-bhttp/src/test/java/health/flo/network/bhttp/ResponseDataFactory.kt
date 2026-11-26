package health.flo.network.bhttp

import health.flo.network.bhttp.model.ResponseControlData

private const val TEST_STATUS_CODE = 200

internal object ResponseDataFactory {

    fun responseControlData(statusCode: Int = TEST_STATUS_CODE) = ResponseControlData(
        statusCode = statusCode,
    )

    fun knownLengthResponseHex(): String = listOf(
        // Framing indicator:
        "01",

        // Response control data:
        "40c8", // Status code(200)

        // Header section:
        "00", // Header section length (0)

        // Content:
        "1d", // Content length (29)
        "5468697320636f6e74656e7420636f6e7461696e732043524c462e0d0a", // Content (This content contains CRLF.)

        // Trailer section:
        "0d", // Trailer section length (13)

        // Field line:
        "07", // Name length(7)
        "747261696c6572", // Name(trailer)
        "04", // Value length (4)
        "74657874", // Value(text)
    ).joinToString(separator = "")

    fun knownLengthResponseTrimmedHex(): String = listOf(
        // Framing indicator:
        "01",

        // Response control data:
        "40c8", // Status code(200)

        // Header section:
        "0c", // Header section length (12)
        // Field line:
        "06", // Name length(6)
        "686561646572", // Name(header)
        "04", // Value length (4)
        "74657874", // Value(text)

        // Content (truncated - only 00 length is present)
        "00",

        // Trailer section (truncated - only 00 length is present)
        "00",
    ).joinToString(separator = "")

    fun knownLengthResponseWithoutTrailerSectionHex(): String = listOf(
        // Framing indicator:
        "01",

        // Response control data:
        "40c8", // Status code(200)

        // Header section:
        "00", // Header section length (0)

        // Content:
        "04", // Content length (4)
        "74657874", // Content (text)

        // Trailer section omitted
    ).joinToString(separator = "")

    fun knownLengthResponseWithoutContentAndTrailerSectionHex(): String = listOf(
        // Framing indicator:
        "01",

        // Response control data:
        "40c8", // Status code(200)

        // Header section:
        "0c", // Header section length (12)
        // Field line:
        "06", // Name length(6)
        "686561646572", // Name(header)
        "04", // Value length (4)
        "74657874", // Value(text)

        // Content and trailer section omitted
    ).joinToString(separator = "")
}
