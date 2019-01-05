package pl.karol202.sock.converter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import pl.karol202.sock.PublicApi

@PublicApi
class JvmJsonEncoder<T : Any>(clazz: Class<T>) : Encoder<T>
{
	private val adapter: JsonAdapter<T> = Moshi.Builder().build().adapter<T>(clazz)

	@PublicApi
	override fun encode(data: T) = adapter.toJson(data).toByteArray()
}

@PublicApi
class JvmJsonDecoder<T : Any>(clazz: Class<T>): Decoder<T>
{
	private val adapter: JsonAdapter<T> = Moshi.Builder().build().adapter<T>(clazz)

	@PublicApi
	override fun decode(bytes: ByteArray): T = adapter.fromJson(bytes.toString()) ?: throw NullPointerException()
}
