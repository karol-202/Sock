package pl.karol202.sock.connection

import pl.karol202.sock.PublicApi
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

@PublicApi
class JvmTCPConnection(private val address: String,
                       private val port: Int) : TCPConnection
{
	private class SocketConnection(val socket: Socket)
	{
		val inputStream: InputStream = socket.getInputStream()
		val outputStream: OutputStream = socket.getOutputStream()
	}

	private var socketConnection: SocketConnection? = null

	@PublicApi
	override val isConnected: Boolean
		get() = socketConnection != null

	@PublicApi
	override fun connect()
	{
		if(isConnected) throw IllegalStateException("Already connected")
		socketConnection = SocketConnection(Socket(address, port))
	}

	@PublicApi
	override fun read(): ByteArray = socketConnection?.inputStream?.readBytes() ?: throw IllegalStateException("Not connected")

	@PublicApi
	override fun send(data: ByteArray)
	{
		socketConnection?.outputStream?.write(data) ?: throw IllegalStateException("Not connected")
	}

	@PublicApi
	override fun close()
	{
		socketConnection?.socket?.close() ?: throw IllegalStateException("Not connected")
		socketConnection = null
	}
}
