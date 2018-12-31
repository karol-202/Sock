package pl.karol202.sock.converter

import pl.karol202.plumber.TransitiveBiLayer
import pl.karol202.sock.PublicApi

@PublicApi
interface Converter<T : Any> : TransitiveBiLayer<ByteArray, T>
{
	override fun transform(input: ByteArray) = decode(input)

	override fun transformBack(input: T) = encode(input)

	@PublicApi
	fun decode(bytes: ByteArray): T

	@PublicApi
	fun encode(data: T): ByteArray
}

