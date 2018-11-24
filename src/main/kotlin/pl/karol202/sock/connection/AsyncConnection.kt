package pl.karol202.sock.connection

interface AsyncConnection
{
	companion object
	{
		fun withCoroutines(connection: Connection) = CoroutinesAsyncConnection(connection)
	}

	fun connect(callback: (Result<Unit>) -> Unit)

	fun read(callback: (Result<ByteArray>) -> Unit)

	fun send(data: ByteArray, callback: (Result<Unit>) -> Unit)

	fun close(callback: (Result<Unit>) -> Unit)
}