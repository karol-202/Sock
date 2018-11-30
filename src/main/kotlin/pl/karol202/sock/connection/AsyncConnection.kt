package pl.karol202.sock.connection

import com.github.kittinunf.result.Result
import kotlinx.coroutines.CoroutineScope
import java.io.IOException

interface AsyncConnection
{
	companion object
	{
		fun withCoroutines(connection: Connection) = CoroutinesAsyncConnection(connection)

		fun withCoroutinesOverTCP(address: String, port: Int) = withCoroutines(TCPConnection(address, port))
	}

	fun connect(coroutineScope: CoroutineScope, callback: (Result<Unit, Exception>) -> Unit)

	fun read(coroutineScope: CoroutineScope, callback: (Result<ByteArray, IOException>) -> Unit)

	fun send(coroutineScope: CoroutineScope, data: ByteArray, callback: (Result<Unit, IOException>) -> Unit)

	fun close(coroutineScope: CoroutineScope, callback: (Result<Unit, IOException>) -> Unit)
}