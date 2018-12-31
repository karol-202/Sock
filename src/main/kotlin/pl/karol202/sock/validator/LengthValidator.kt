package pl.karol202.sock.validator

import pl.karol202.sock.PublicApi
import java.nio.ByteBuffer

@PublicApi
abstract class LengthValidator : Validator
{
	@PublicApi
	override fun applyValidationData(data: ByteArray): ByteArray = data.size.toByteArray() + data

	protected fun ByteArray.toInt() = if(this.size >= 4) ByteBuffer.wrap(this).int else null

	private fun Int.toByteArray(): ByteArray = ByteBuffer.allocate(4).putInt(this).array()
}

@PublicApi
class DiscardingLengthValidator : LengthValidator()
{
	@PublicApi
	override fun validate(data: ByteArray): ByteArray?
	{
		val expectedLength = data.take(4).toByteArray().toInt() ?: return null
		val packet = data.drop(4)
		return if(packet.size == expectedLength) packet.toByteArray() else null
	}
}

@PublicApi
class BufferedLengthValidator : LengthValidator()
{
	private var expectedLength: Int? = null
	private var buffer = listOf<Byte>()

	@PublicApi
	override fun validate(data: ByteArray): ByteArray?
	{
		if(expectedLength == null)
		{
			buffer = data.toList()
			readExpectedLength()
			if(expectedLength == null) return null
		}
		else buffer += data.toList()
		return checkBuffer()
	}

	private fun readExpectedLength()
	{
		expectedLength = buffer.take(4).toByteArray().toInt()?.also { buffer = buffer.drop(4) }
		//Delete first 4 bytes if int has been read successfully
	}

	private fun checkBuffer(): ByteArray?
	{
		val expectedLength = expectedLength ?: return null
		if(buffer.size < expectedLength) return null

		val packet = buffer.take(expectedLength)
		buffer = buffer.drop(expectedLength)
		return packet.toByteArray()
	}
}
