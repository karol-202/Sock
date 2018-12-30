package pl.karol202.sock.convert

import com.github.kittinunf.result.Result
import pl.karol202.plumber.unidirectional.TransitiveLayer

interface Encoder<T : Any> : TransitiveLayer<T, ByteArray>
{
	fun encode(data: T): ByteArray

	override fun transform(input: T): ByteArray = encode(input)
}

interface Decoder<T : Any> : TransitiveLayer<ByteArray, Result<T, Exception>>
{
	fun decode(bytes: ByteArray): Result<T, Exception>

	override fun transform(input: ByteArray): Result<T, Exception> = decode(input)
}
