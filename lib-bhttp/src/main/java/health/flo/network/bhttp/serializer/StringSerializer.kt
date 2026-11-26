package health.flo.network.bhttp.serializer

import health.flo.network.bhttp.BhttpDefaults
import health.flo.network.bhttp.UnencodableCharacterException
import java.nio.CharBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.Charset
import java.nio.charset.CodingErrorAction

internal fun String.binaryEncodedWithLengthPrefix(charset: Charset = BhttpDefaults.charset): ByteArray {
    return encodeStrictly(charset)
        .withLengthPrefix()
}

// String.toByteArray replaces a character the charset can't encode with '?'.
private fun String.encodeStrictly(charset: Charset): ByteArray {
    val encoder = charset.newEncoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT)
    val buffer = try {
        encoder.encode(CharBuffer.wrap(this))
    } catch (exception: CharacterCodingException) {
        throw UnencodableCharacterException(charset, exception)
    }

    return ByteArray(buffer.remaining()).also(buffer::get)
}
