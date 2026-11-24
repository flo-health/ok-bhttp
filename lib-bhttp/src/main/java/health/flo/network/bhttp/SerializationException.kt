package health.flo.network.bhttp

open class SerializationException(message: String, cause: Throwable? = null) : Exception(message, cause)

class NumberOutOfRangeException : SerializationException("Specified number is out of range 0..4611686018427387903")

class DuplexBodyException : SerializationException("A duplex request body can't be encoded as known-length BHTTP")

class EndOfArrayException : SerializationException("Unable to read the next bytes from the array")

class IntOverflowException(
    number: Long,
) : SerializationException("Number $number doesn't fit in Int")

class UnknownFramingIndicatorException(
    indicator: Int,
) : SerializationException("Framing indicator $indicator is not supported")

class UnknownStatusCodeException(
    statusCode: Int,
) : SerializationException("Unknown status code: $statusCode")
