package pl.karol202.sock.connection

import com.github.kittinunf.result.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

class CoroutinesAsyncConnection internal constructor(private val connection: Connection) : AsyncConnection
{
	override fun connect(coroutineScope: CoroutineScope, callback: (Result<Unit, Exception>) -> Unit)
	{
		coroutineScope.launch {
			callback(Result.of { connection.connect() })
		}
	}

	override fun read(coroutineScope: CoroutineScope, callback: (Result<ByteArray, IOException>) -> Unit)
	{
		coroutineScope.launch {
			callback(Result.of { connection.read() })
		}
	}

	override fun send(coroutineScope: CoroutineScope, data: ByteArray, callback: (Result<Unit, IOException>) -> Unit)
	{
		coroutineScope.launch {
			callback(Result.of { connection.send(data) })
		}
	}

	override fun close(coroutineScope: CoroutineScope, callback: (Result<Unit, IOException>) -> Unit)
	{
		coroutineScope.launch {
			callback(Result.of { connection.close() })
		}
	}
}