package pl.karol202.sock.convert

import com.github.kittinunf.result.Result

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

class JsonEncoder<T : Any>(clazz: Class<T>): Encoder<T>
{
	companion object
	{
		inline fun <reified T : Any> create() = JsonEncoder(T::class.java)
	}

	private val adapter: JsonAdapter<T> = Moshi.Builder().build().adapter<T>(clazz)

	override fun encode(data: T) = adapter.toJson(data).toByteArray()
}

class JsonDecoder<T : Any>(clazz: Class<T>): Decoder<T>
{
	companion object
	{
		inline fun <reified T : Any> create() = JsonDecoder(T::class.java)
	}

	private val adapter: JsonAdapter<T> = Moshi.Builder().build().adapter<T>(clazz)

	override fun decode(bytes: ByteArray) =
			Result.of<T, Exception> { adapter.fromJson(bytes.toString()) ?: throw NullPointerException() }
}
