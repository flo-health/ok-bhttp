# Kotlin Binary HTTP Implementation

This project is a Kotlin language implementation of the Binary HTTP format, according
to [RFC9292 "Binary Representation of HTTP Messages"](https://datatracker.ietf.org/doc/rfc9292/). It allows
serializing [OkHttp](https://github.com/square/okhttp) `Request` and `Response` into binary data and back from it. This
allows for encoding HTTP messages that can be conveyed outside of the HTTP protocol, for example, to use
in [Oblivious HTTP](https://datatracker.ietf.org/doc/draft-ietf-ohai-ohttp/).

## Download

#### Declare Gradle dependencies

```kotlin
dependencies {
    implementation("com.github.flohealth:ok-bhttp:0.1.0")
}
```

#### Download artifacts

You can download JAR artifacts from [GitHub Releases](https://github.com/flohealth/ok-bhttp/releases).

## Usage

#### Serialize request

```kotlin
import health.flo.network.bhttp.OkHttpBinarySerializer
import health.flo.network.bhttp.RequestBinaryData
import health.flo.network.bhttp.ResponseBinaryData
import okhttp3.Request

val binarySerializer: OkHttpBinarySerializer = OkHttpBinarySerializer.create()
val request: Request
...
val serializedRequest: RequestBinaryData = binarySerializer.serialize(request)
val serializedBinary: ByteArray = serializedRequest.data
```

#### Deserialize response

```kotlin
import health.flo.network.bhttp.OkHttpBinarySerializer
import health.flo.network.bhttp.ResponseBinaryData
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response

val binarySerializer: OkHttpBinarySerializer = OkHttpBinarySerializer.create()
...
val request: Request // original request
val protocol: Protocol
val responseBinary: ByteArray // serialized response

val deserializedResponse: Response = binarySerializer.deserialize(
    binaryData = ResponseBinaryData(responseBinary),
    protocol = protocol,
    request = request,
)
```

## Feature support

Binary HTTP [specification](https://datatracker.ietf.org/doc/rfc9292/) defines two formats of messages (meaning both
requests and responses): _Known-Length_ Messages and _Indeterminate-Length_ Messages. Ideally, this library could
support serialization and deserialization of both formats for both request and response. Current support is limited to _
Known-Length_ Messages only and two possible translations that are required by client applications:

1. Serialization of OkOhttp `Request` into Known-Length Request
2. Deserialization of OkOhttp `Response` from Known-Length Response

#### Supported translations matrix

| Message Type                  | To Binary | From Binary |
| ----------------------------- |:---------:| :----------:|
| Known-Length Request          | supported |      X      |
| Known-Length Response         |     X     |  supported  |
| Indeterminate-Length Request  |     X     |      X      |
| Indeterminate-Length Response |     X     |      X      |

## Limitations

#### Request bodies

Known-length BHTTP needs the whole request body before the request is sent, so the body is held in memory. A duplex
body can't be encoded and throws `DuplexBodyException`.

#### HTTP Fields encoding

This library uses ASCII to encode and decode string data for URL and HTTP header fields and their values. It does not
implement automatic encoding of any non-ASCII characters into ASCII and will throw an error if it encounters any. In
particular:

- If a non-ASCII character is encountered in the URL or a header field (field name or value) of an `URLRequest`
  , `RequestCreationError` will be thrown, indicating the point of failure
- If header data of a response cannot be decoded into String using ASCII, `BinaryHTTPDecodingError.asciiDecodingError`
  will be thrown
  If you still need to pass non-ASCII values, encode them into ASCII **before** calling this library. For example, use
  percent-encoding for request URLs, etc.

## License

Released under [**MIT License**](LICENSE.txt).
