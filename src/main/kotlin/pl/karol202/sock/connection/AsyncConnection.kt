package pl.karol202.sock.connection

import com.github.kittinunf.result.Result
import java.io.IOException

interface AsyncConnection
{
	companion object
	{
		fun withCoroutines(connection: Connection) = CoroutinesAsyncConnection(connection)
	}

	fun connect(callback: (Result<Unit, Exception>) -> Unit)

	fun read(callback: (Result<ByteArray, IOException>) -> Unit)

	fun send(data: ByteArray, callback: (Result<Unit, IOException>) -> Unit)

	fun close(callback: (Result<Unit, IOException>) -> Unit)
}