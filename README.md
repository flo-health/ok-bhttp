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
    implementation("com.github.flohealth:ok-bhttp:0.2.0")
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

This library encodes and decodes the request control data and header fields as UTF-8, as OkHttp does on a direct call.
In particular:

- A header value that isn't ASCII, such as `café`, goes through as its UTF-8 bytes in both directions. Response
  headers are added with `Headers.Builder.addUnsafeNonAscii`, so OkHttp accepts them.
- In a response, bytes that aren't valid UTF-8 become U+FFFD, as on a direct call.
- A request string with a lone surrogate can't be encoded and throws `UnencodableCharacterException`.

A response fails with `InvalidHeaderException` when a header name isn't ASCII, since OkHttp accepts only ASCII names,
or when a header value has CR, LF or NUL.

## License

Released under [**MIT License**](LICENSE.txt).
