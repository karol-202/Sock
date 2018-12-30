package pl.karol202.sock.connection

import pl.karol202.plumber.bidirectional.TerminalBiLayer
import pl.karol202.sock.PublicApi

@PublicApi
interface Connection : TerminalBiLayer<ByteArray>
{
	@PublicApi
	fun connect()

	@PublicApi
	fun read(): ByteArray

	@PublicApi
	fun send(data: ByteArray)

	@PublicApi
	fun close()

	override fun transform(input: ByteArray) = send(input)

	override fun transformBack(input: Unit) = read()
}
