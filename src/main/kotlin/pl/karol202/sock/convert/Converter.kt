package pl.karol202.sock.convert

import com.github.kittinunf.result.Result

interface Converter<T : Any>
{
	fun encode(data: T): ByteArray

	fun decode(bytes: ByteArray): Result<T, Exception>
}