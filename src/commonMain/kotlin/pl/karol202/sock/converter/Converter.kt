package pl.karol202.sock.converter

import pl.karol202.plumber.TransitiveLayer
import pl.karol202.sock.PublicApi

@PublicApi
interface Encoder<T : Any> : TransitiveLayer<T, ByteArray>
{
	@PublicApi
	fun encode(data: T): ByteArray

	override fun transform(input: T) = encode(input)
}

@PublicApi
interface Decoder<T : Any> : TransitiveLayer<ByteArray, T>
{
	@PublicApi
	fun decode(bytes: ByteArray): T

	override fun transform(input: ByteArray) = decode(input)
}

@PublicApi
expect object ConverterFactory
{
	@PublicApi
	inline fun <reified T : Any> createJsonEncoder(): Encoder<T>

	@PublicApi
	inline fun <reified T : Any> createJsonDecoder(): Decoder<T>
}
