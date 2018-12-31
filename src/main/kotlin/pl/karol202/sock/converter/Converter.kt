package pl.karol202.sock.converter

import com.github.kittinunf.result.Result
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
interface Decoder<T : Any> : TransitiveLayer<ByteArray, Result<T, Exception>>
{
	@PublicApi
	fun decode(bytes: ByteArray): Result<T, Exception>

	override fun transform(input: ByteArray) = decode(input)
}
