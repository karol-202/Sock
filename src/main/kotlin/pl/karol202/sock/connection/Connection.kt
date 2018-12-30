package pl.karol202.sock.connection

import pl.karol202.plumber.bidirectional.TerminalBiLayer

interface Connection : TerminalBiLayer<ByteArray>
{
	fun connect()

	fun read(): ByteArray

	fun send(data: ByteArray)

	fun close()

	override fun transform(input: ByteArray) = send(input)

	override fun transformBack(input: Unit) = read()
}
