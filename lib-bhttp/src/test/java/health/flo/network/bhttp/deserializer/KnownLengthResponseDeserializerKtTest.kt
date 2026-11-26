package health.flo.network.bhttp.deserializer

import health.flo.network.bhttp.ResponseDataFactory.knownLengthResponseHex
import health.flo.network.bhttp.ResponseDataFactory.knownLengthResponseTrimmedHex
import health.flo.network.bhttp.ResponseDataFactory.knownLengthResponseWithoutContentAndTrailerSectionHex
import health.flo.network.bhttp.ResponseDataFactory.knownLengthResponseWithoutTrailerSectionHex
import health.flo.network.bhttp.model.FieldLine
import health.flo.network.bhttp.model.FieldSection
import health.flo.network.bhttp.model.KnownLengthResponse.Companion.FRAMING_INDICATOR
import health.flo.network.bhttp.parseHexEncodedByteArray
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class KnownLengthResponseDeserializerKtTest {

    @Test
    fun `readKnownLengthResponse should deserialize full response correctly`() {
        val responseHex = knownLengthResponseHex()
        val reader = ByteArrayReader.fromByteArray(responseHex.parseHexEncodedByteArray())
        val expectedFramingIndicator = FRAMING_INDICATOR
        val expectedStatusCode = 200
        val expectedContent = "This content contains CRLF.\r\n"
        val expectedTrailerSection = FieldSection(listOf(FieldLine("trailer", "text")))

        val framingIndicator = reader.readInt()
        val actual = reader.readKnownLengthResponse()

        assertThat(framingIndicator).isEqualTo(expectedFramingIndicator)
        assertThat(actual.controlData.statusCode).isEqualTo(expectedStatusCode)
        assertThat(actual.headerSection.fieldLines).isEmpty()
        assertThat(String(actual.content, Charsets.US_ASCII)).isEqualTo(expectedContent)
        assertThat(actual.trailerSection).isEqualTo(expectedTrailerSection)
    }

    @Test
    fun `readKnownLengthResponse should deserialize trimmed response correctly`() {
        val responseHex = knownLengthResponseTrimmedHex()
        val reader = ByteArrayReader.fromByteArray(responseHex.parseHexEncodedByteArray())
        val expectedFramingIndicator = FRAMING_INDICATOR
        val expectedStatusCode = 200
        val expectedHeaderSection = FieldSection(listOf(FieldLine("header", "text")))

        val framingIndicator = reader.readInt()
        val actual = reader.readKnownLengthResponse()

        assertThat(framingIndicator).isEqualTo(expectedFramingIndicator)
        assertThat(actual.controlData.statusCode).isEqualTo(expectedStatusCode)
        assertThat(actual.headerSection).isEqualTo(expectedHeaderSection)
        assertThat(actual.content).isEmpty()
        assertThat(actual.trailerSection.fieldLines).isEmpty()
    }

    @Test
    fun `readKnownLengthResponse should treat omitted trailer section as empty`() {
        val responseHex = knownLengthResponseWithoutTrailerSectionHex()
        val reader = ByteArrayReader.fromByteArray(responseHex.parseHexEncodedByteArray())

        reader.readInt()
        val actual = reader.readKnownLengthResponse()

        assertThat(actual.controlData.statusCode).isEqualTo(200)
        assertThat(actual.headerSection.fieldLines).isEmpty()
        assertThat(String(actual.content, Charsets.US_ASCII)).isEqualTo("text")
        assertThat(actual.trailerSection.fieldLines).isEmpty()
    }

    @Test
    fun `readKnownLengthResponse should treat omitted content and trailer section as empty`() {
        val responseHex = knownLengthResponseWithoutContentAndTrailerSectionHex()
        val reader = ByteArrayReader.fromByteArray(responseHex.parseHexEncodedByteArray())
        val expectedHeaderSection = FieldSection(listOf(FieldLine("header", "text")))

        reader.readInt()
        val actual = reader.readKnownLengthResponse()

        assertThat(actual.controlData.statusCode).isEqualTo(200)
        assertThat(actual.headerSection).isEqualTo(expectedHeaderSection)
        assertThat(actual.content).isEmpty()
        assertThat(actual.trailerSection.fieldLines).isEmpty()
    }
}
