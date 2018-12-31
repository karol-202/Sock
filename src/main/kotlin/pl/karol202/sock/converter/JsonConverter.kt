package pl.karol202.sock.converter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import pl.karol202.sock.PublicApi

@PublicApi
class JsonConverter<T : Any>(clazz: Class<T>) : Converter<T>
{
	companion object
	{
		@PublicApi
		inline fun <reified T : Any> create() = JsonConverter(T::class.java)
	}

	private val adapter: JsonAdapter<T> = Moshi.Builder().build().adapter<T>(clazz)

	@PublicApi
	override fun decode(bytes: ByteArray): T = adapter.fromJson(bytes.toString())!!

	@PublicApi
	override fun encode(data: T): ByteArray = adapter.toJson(data).toByteArray()
}
