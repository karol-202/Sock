package pl.karol202.sock.converter

import pl.karol202.sock.PublicApi

@PublicApi
actual object ConverterFactory
{
	@PublicApi
	actual inline fun <reified T : Any> createJsonEncoder(): Encoder<T> = JvmJsonEncoder(T::class.java)

	@PublicApi
	actual inline fun <reified T : Any> createJsonDecoder(): Decoder<T> = JvmJsonDecoder(T::class.java)
}