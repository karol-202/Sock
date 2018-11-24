package pl.karol202.sock.connection

import com.github.kittinunf.result.Result
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class CoroutinesAsyncConnection internal constructor(private val connection: Connection) : AsyncConnection
{
	override fun connect(callback: (Result<Unit, Exception>) -> Unit)
	{
		GlobalScope.launch {
			callback(Result.of { connection.connect() })
		}
	}

	override fun read(callback: (Result<ByteArray, IOException>) -> Unit)
	{
		GlobalScope.launch {
			callback(Result.of { connection.read() })
		}
	}

	override fun send(data: ByteArray, callback: (Result<Unit, IOException>) -> Unit)
	{
		GlobalScope.launch {
			callback(Result.of { connection.send(data) })
		}
	}

	override fun close(callback: (Result<Unit, IOException>) -> Unit)
	{
		GlobalScope.launch {
			callback(Result.of { connection.close() })
		}
	}
}