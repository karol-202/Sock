package pl.karol202.sock.connection

interface Connection
{
	fun connect()

	fun read(): ByteArray

	fun send(data: ByteArray)

	fun close()
}