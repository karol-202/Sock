package pl.karol202.sock.connection

import pl.karol202.sock.PublicApi

@PublicApi
actual object ConnectionFactory
{
	@PublicApi
	actual fun createTCPConnection(address: String, port: Int): TCPConnection = JvmTCPConnection(address, port)
}