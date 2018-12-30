package pl.karol202.sock.connection

import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class TCPConnection(private val address: String,
                    private val port: Int) : Connection
{
	private class SocketConnection(val socket: Socket)
	{
		val inputStream: InputStream = socket.getInputStream()
		val outputStream: OutputStream = socket.getOutputStream()
	}

	private var socketConnection: SocketConnection? = null

	val isConnected: Boolean
		get() = socketConnection != null

	override fun connect()
	{
		if(isConnected) throw IllegalStateException("Already connected")
		socketConnection = SocketConnection(Socket(address, port))
	}

	override fun read(): ByteArray = socketConnection?.inputStream?.readBytes() ?: throw IllegalStateException("Not connected")

	override fun send(data: ByteArray)
	{
		socketConnection?.outputStream?.write(data) ?: throw IllegalStateException("Not connected")
	}

	override fun close()
	{
		socketConnection?.socket?.close() ?: throw IllegalStateException("Not connected")
		socketConnection = null
	}
}
