package pl.karol202.sock.connection

import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class TCPConnection(private val address: String,
                    private val port: Int) : Connection
{
	private var socket: Socket? = null
	private var inputStream: InputStream? = null
	private var outputStream: OutputStream? = null

	override fun connect()
	{
		val socket = Socket(address, port)
		this.socket = socket
		inputStream = socket.getInputStream()
		outputStream = socket.getOutputStream()
	}

	override fun read() = inputStream?.readBytes() ?: throw IllegalStateException("Not connected")

	override fun send(data: ByteArray)
	{
		outputStream?.write(data) ?: throw IllegalStateException("Not connected")
	}

	override fun close()
	{
		socket?.close() ?: throw IllegalStateException("Not connected")
		socket = null
		inputStream = null
		outputStream = null
	}
}