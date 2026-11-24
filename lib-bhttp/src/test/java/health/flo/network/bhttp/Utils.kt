package health.flo.network.bhttp

fun ByteArray.hexEncodedString(): String {
    return this.joinToString(separator = "") { byte -> "%02x".format(byte) }
}

fun String.parseHexEncodedByteArray(): ByteArray = hexToByteArray()

fun String.parseHexEncodedByte(): Byte = substring(0, 2).hexToByte()
