package pl.karol202.sock

interface Connection
{
	fun connect()

	fun read(): ByteArray?

	fun send(data: ByteArray)

	fun close()
}