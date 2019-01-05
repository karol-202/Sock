package pl.karol202.sock.validator

import pl.karol202.sock.PublicApi

@PublicApi
actual object ValidatorFactory
{
	@PublicApi
	actual fun createDiscardingLengthValidator(): Validator = JvmDiscardingLengthValidator()

	@PublicApi
	actual fun createBufferedLengthValidator(): Validator = JvmBufferedLengthValidator()
}

