package pl.karol202.sock.connection

import pl.karol202.sock.PublicApi
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

//Timeout is in milliseconds, 0 means no timeout (infinite waiting)
@PublicApi
class TCPConnection(private val address: String,
                    private val port: Int,
                    private val timeout: Int = DEFAULT_TIMEOUT) : Connection
{
	companion object
	{
		private const val DEFAULT_TIMEOUT = 1000
	}

	private class SocketConnection(val socket: Socket)
	{
		val inputStream: InputStream = socket.getInputStream()
		val outputStream: OutputStream = socket.getOutputStream()
	}

	private var socketConnection: SocketConnection? = null

	@PublicApi
	val isConnected: Boolean
		get() = socketConnection != null

	@PublicApi
	override fun connect()
	{
		if(isConnected) throw IllegalStateException("Already connected")
		socketConnection = SocketConnection(createSocket())
	}

	private fun createSocket() = Socket().apply {
		connect(createAddress(), timeout)
	}

	private fun createAddress() = InetSocketAddress(address, port)

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
