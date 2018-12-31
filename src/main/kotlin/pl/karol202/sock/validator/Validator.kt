package pl.karol202.sock.validator

import pl.karol202.plumber.Output
import pl.karol202.plumber.TransitiveBiLayerWithFlowControl
import pl.karol202.sock.PublicApi

@PublicApi
interface Validator : TransitiveBiLayerWithFlowControl<ByteArray, ByteArray>
{
	override fun transformWithFlowControl(input: ByteArray): Output<ByteArray>
	{
		return validate(input)?.let { Output.Value(it) } ?: Output.NoValue()
	}

	override fun transformBackWithFlowControl(input: ByteArray) = Output.Value(applyValidationData(input))

	@PublicApi
	fun validate(data: ByteArray): ByteArray?

	@PublicApi
	fun applyValidationData(data: ByteArray): ByteArray
}
