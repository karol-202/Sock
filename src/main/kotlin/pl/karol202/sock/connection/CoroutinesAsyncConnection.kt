package pl.karol202.sock.connection

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CoroutinesAsyncConnection internal constructor(private val connection: Connection) : AsyncConnection
{
	override fun connect(callback: (Result<Unit>) -> Unit)
	{
		GlobalScope.launch {
			callback(runCatching { connection.connect() })
		}
	}

	override fun read(callback: (Result<ByteArray>) -> Unit)
	{
		GlobalScope.launch {
			callback(runCatching { connection.read() })
		}
	}

	override fun send(data: ByteArray, callback: (Result<Unit>) -> Unit)
	{
		GlobalScope.launch {
			callback(runCatching { connection.send(data) })
		}
	}

	override fun close(callback: (Result<Unit>) -> Unit)
	{
		GlobalScope.launch {
			callback(runCatching { connection.close() })
		}
	}
}