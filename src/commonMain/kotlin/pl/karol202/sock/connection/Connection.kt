package pl.karol202.sock.connection

import pl.karol202.plumber.TerminalBiLayer
import pl.karol202.sock.PublicApi

@PublicApi
interface Connection : TerminalBiLayer<ByteArray>
{
	@PublicApi
	fun read(): ByteArray

	@PublicApi
	fun send(data: ByteArray)

	override fun transform(input: ByteArray) = send(input)

	override fun transformBack(input: Unit) = read()
}

@PublicApi
expect object ConnectionFactory
{
	@PublicApi
	fun createTCPConnection(address: String, port: Int): TCPConnection
}
