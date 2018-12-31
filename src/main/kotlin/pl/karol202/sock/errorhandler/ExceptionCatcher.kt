package pl.karol202.sock.errorhandler

import pl.karol202.plumber.*
import pl.karol202.sock.PublicApi
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@PublicApi
abstract class ExceptionCatcher internal constructor(protected val exceptions: List<ExceptionEntry<*>>)
{
	companion object
	{
		@PublicApi
		fun uniPipelineBuilder(): UniPipelineBuilder = UniPipelineBuilder()

		@PublicApi
		fun biPipelineBuilder(): BiPipelineBuilder = BiPipelineBuilder()
	}

	data class ExceptionEntry<E : Exception> internal constructor(val klass: KClass<E>,
	                                                              val handler: (E) -> Unit)

	@PublicApi
	abstract class Builder protected constructor()
	{
		protected val exceptions = mutableListOf<ExceptionEntry<*>>()

		@PublicApi
		inline fun <reified E: Exception> addHandler(noinline handler: (E) -> Unit) : Unit = addHandler(E::class, handler)

		@PublishedApi
		internal fun <E: Exception> addHandler(klass: KClass<E>, handler: (E) -> Unit)
		{
			exceptions.add(ExceptionEntry(klass, handler))
		}
	}

	@PublicApi
	class UniPipelineBuilder internal constructor() : Builder()
	{
		private fun <I, O> createLayer(pipeline: UniPipeline<I, O>) = UniPipelineExceptionCatcher(pipeline, exceptions)

		@PublicApi
		fun <I, O> createPipeline(pipeline: OpenUniPipeline<I, O>): OpenUniPipeline<I, O> =
				createLayer(pipeline).toOpenUniPipeline()

		@PublicApi
		fun <O> createPipeline(pipeline: LeftClosedUniPipeline<O, *>): LeftClosedUniPipeline<O, Unit> =
				PlaceholderUniLayer + createLayer(pipeline).toOpenUniPipeline()

		@PublicApi
		fun <I> createPipeline(pipeline: RightClosedUniPipeline<I, *>): RightClosedUniPipeline<I, *> =
				createLayer(pipeline).toOpenUniPipeline() + PlaceholderUniLayer

		@PublicApi
		fun createPipeline(pipeline: ClosedUniPipeline): ClosedUniPipeline =
				PlaceholderUniLayer + createLayer(pipeline).toOpenUniPipeline() + PlaceholderUniLayer
	}

	@PublicApi
	class BiPipelineBuilder internal constructor() : Builder()
	{
		private fun <I, O> createLayer(pipeline: BiPipeline<I, O>) = BiPipelineExceptionCatcher(pipeline, exceptions)

		@PublicApi
		fun <I, O> createPipeline(pipeline: OpenBiPipeline<I, O>): OpenBiPipeline<I, O> =
				createLayer(pipeline).toOpenBiPipeline()

		@PublicApi
		fun <O> createPipeline(pipeline: LeftClosedBiPipeline<O, *>): LeftClosedBiPipeline<O, Unit> =
				PlaceholderBiLayer + createLayer(pipeline).toOpenBiPipeline()

		@PublicApi
		fun <I> createPipeline(pipeline: RightClosedBiPipeline<I, *>): RightClosedBiPipeline<I, *> =
				createLayer(pipeline).toOpenBiPipeline() + PlaceholderBiLayer

		@PublicApi
		fun createPipeline(pipeline: ClosedBiPipeline): ClosedBiPipeline =
				PlaceholderBiLayer + createLayer(pipeline).toOpenBiPipeline() + PlaceholderBiLayer
	}

	private object PlaceholderUniLayer : CreatorLayer<Unit>, ConsumerLayer<Unit>
	{
		override fun transform(input: Unit) { }
	}

	private object PlaceholderBiLayer : TerminalBiLayer<Unit>
	{
		override fun transform(input: Unit) { }

		override fun transformBack(input: Unit) { }
	}

	protected inline fun <reified E : Exception> handleException(exception: E)
	{
		val entry = exceptions.firstOrNull { E::class.isSubclassOf(it.klass) } as ExceptionEntry<E>?
		entry?.handler?.invoke(exception) ?: throw exception
	}
}

internal class UniPipelineExceptionCatcher<I, O> internal constructor(private val pipeline: UniPipeline<I, O>,
                                                                     exceptions: List<ExceptionEntry<*>>) :
		ExceptionCatcher(exceptions), TransitiveLayerWithFlowControl<I, O>
{
	@PublicApi
	override fun transformWithFlowControl(input: I): Output<O> = try
	{
		pipeline.transform(input)
	}
	catch(e: Exception)
	{
		handleException(e)
		Output.NoValue()
	}
}

internal class BiPipelineExceptionCatcher<I, O> internal constructor(private val pipeline: BiPipeline<I, O>,
                                                                    exceptions: List<ExceptionEntry<*>>) :
		ExceptionCatcher(exceptions), TransitiveBiLayerWithFlowControl<I, O>
{
	@PublicApi
	override fun transformWithFlowControl(input: I): Output<O> = try
	{
		pipeline.transform(input)
	}
	catch(e: Exception)
	{
		handleException(e)
		Output.NoValue()
	}

	@PublicApi
	override fun transformBackWithFlowControl(input: O): Output<I> = try
	{
		pipeline.transformBack(input)
	}
	catch(e: Exception)
	{
		handleException(e)
		Output.NoValue()
	}
}
