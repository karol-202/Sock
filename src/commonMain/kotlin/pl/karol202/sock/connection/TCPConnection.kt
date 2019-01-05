package pl.karol202.sock.connection

import pl.karol202.sock.PublicApi

@PublicApi
interface TCPConnection : Connection
{
	@PublicApi
	val isConnected: Boolean

	@PublicApi
	fun connect()

	@PublicApi
	fun close()
}
