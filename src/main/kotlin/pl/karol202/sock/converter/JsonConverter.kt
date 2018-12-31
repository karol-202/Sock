package pl.karol202.sock.converter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import pl.karol202.sock.PublicApi

@PublicApi
class JsonEncoder<T : Any>(clazz: Class<T>): Encoder<T>
{
	companion object
	{
		@PublicApi
		inline fun <reified T : Any> create() = JsonEncoder(T::class.java)
	}

	private val adapter: JsonAdapter<T> = Moshi.Builder().build().adapter<T>(clazz)

	@PublicApi
	override fun encode(data: T) = adapter.toJson(data).toByteArray()
}

@PublicApi
class JsonDecoder<T : Any>(clazz: Class<T>): Decoder<T>
{
	companion object
	{
		@PublicApi
		inline fun <reified T : Any> create() = JsonDecoder(T::class.java)
	}

	private val adapter: JsonAdapter<T> = Moshi.Builder().build().adapter<T>(clazz)

	@PublicApi
	override fun decode(bytes: ByteArray): T = adapter.fromJson(bytes.toString()) ?: throw NullPointerException()
}
