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
		fun <I, O> builder(pipeline: OpenUniPipeline<I, O>): UniPipelineBuilder<I, O, OpenUniPipeline<I, O>> =
				OpenUniPipelineBuilder(pipeline)

		@PublicApi
		fun <O> builder(pipeline: LeftClosedUniPipeline<O, *>): UniPipelineBuilder<Unit, O, LeftClosedUniPipeline<O, *>> =
				LeftClosedUniPipelineBuilder(pipeline)

		@PublicApi
		fun <I> builder(pipeline: RightClosedUniPipeline<I, *>): UniPipelineBuilder<I, Unit, RightClosedUniPipeline<I, *>> =
				RightClosedUniPipelineBuilder(pipeline)

		@PublicApi
		fun builder(pipeline: ClosedUniPipeline): UniPipelineBuilder<Unit, Unit, ClosedUniPipeline> =
				ClosedUniPipelineBuilder(pipeline)

		@PublicApi
		fun <I, O> builder(pipeline: OpenBiPipeline<I, O>): BiPipelineBuilder<I, O, OpenBiPipeline<I, O>> =
				OpenBiPipelineBuilder(pipeline)

		@PublicApi
		fun <O> builder(pipeline: LeftClosedBiPipeline<O, *>): BiPipelineBuilder<Unit, O, LeftClosedBiPipeline<O, *>> =
				LeftClosedBiPipelineBuilder(pipeline)

		@PublicApi
		fun <I> builder(pipeline: RightClosedBiPipeline<I, *>): BiPipelineBuilder<I, Unit, RightClosedBiPipeline<I, *>> =
				RightClosedBiPipelineBuilder(pipeline)

		@PublicApi
		fun builder(pipeline: ClosedBiPipeline): BiPipelineBuilder<Unit, Unit, ClosedBiPipeline> =
				ClosedBiPipelineBuilder(pipeline)
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

		internal abstract fun createLayer(): ExceptionCatcher
	}

	@PublicApi
	abstract class UniPipelineBuilder<I, O, P : UniPipeline<I, O>> protected constructor(private val pipeline: UniPipeline<I, O>) :
			Builder()
	{
		@PublicApi
		abstract fun createPipeline(): P

		override fun createLayer() = UniPipelineExceptionCatcher(pipeline, exceptions)
	}

	private class OpenUniPipelineBuilder<I, O> @PublishedApi internal constructor(pipeline: UniPipeline<I, O>) :
			UniPipelineBuilder<I, O, OpenUniPipeline<I, O>>(pipeline)
	{
		@PublicApi
		override fun createPipeline(): OpenUniPipeline<I, O> = createLayer().toOpenUniPipeline()
	}

	private class LeftClosedUniPipelineBuilder<O> @PublishedApi internal constructor(pipeline: LeftClosedUniPipeline<O, *>) :
			UniPipelineBuilder<Unit, O, LeftClosedUniPipeline<O, *>>(pipeline)
	{
		@PublicApi
		override fun createPipeline(): LeftClosedUniPipeline<O, Unit> = PlaceholderUniLayer + createLayer().toOpenUniPipeline()
	}

	private class RightClosedUniPipelineBuilder<I> @PublishedApi internal constructor(pipeline: RightClosedUniPipeline<I, *>) :
			UniPipelineBuilder<I, Unit, RightClosedUniPipeline<I, *>>(pipeline)
	{
		@PublicApi
		override fun createPipeline(): RightClosedUniPipeline<I, *> = createLayer().toOpenUniPipeline() + PlaceholderUniLayer
	}

	private class ClosedUniPipelineBuilder @PublishedApi internal constructor(pipeline: ClosedUniPipeline) :
			UniPipelineBuilder<Unit, Unit, ClosedUniPipeline>(pipeline)
	{
		@PublicApi
		override fun createPipeline(): ClosedUniPipeline = PlaceholderUniLayer + createLayer().toOpenUniPipeline() + PlaceholderUniLayer
	}

	@PublicApi
	abstract class BiPipelineBuilder<I, O, P : BiPipeline<I, O>> protected constructor(private val pipeline: BiPipeline<I, O>) :
		Builder()
	{
		@PublicApi
		abstract fun createPipeline(): P

		override fun createLayer() = BiPipelineExceptionCatcher(pipeline, exceptions)
	}

	private class OpenBiPipelineBuilder<I, O> @PublishedApi internal constructor(pipeline: BiPipeline<I, O>) :
			BiPipelineBuilder<I, O, OpenBiPipeline<I, O>>(pipeline)
	{
		@PublicApi
		override fun createPipeline(): OpenBiPipeline<I, O> = createLayer().toOpenBiPipeline()
	}

	private class LeftClosedBiPipelineBuilder<O> @PublishedApi internal constructor(pipeline: LeftClosedBiPipeline<O, *>) :
			BiPipelineBuilder<Unit, O, LeftClosedBiPipeline<O, *>>(pipeline)
	{
		@PublicApi
		override fun createPipeline(): LeftClosedBiPipeline<O, Unit> = PlaceholderBiLayer + createLayer().toOpenBiPipeline()
	}

	private class RightClosedBiPipelineBuilder<I> @PublishedApi internal constructor(pipeline: RightClosedBiPipeline<I, *>) :
			BiPipelineBuilder<I, Unit, RightClosedBiPipeline<I, *>>(pipeline)
	{
		@PublicApi
		override fun createPipeline(): RightClosedBiPipeline<I, *> = createLayer().toOpenBiPipeline() + PlaceholderBiLayer
	}

	private class ClosedBiPipelineBuilder @PublishedApi internal constructor(pipeline: ClosedBiPipeline) :
			BiPipelineBuilder<Unit, Unit, ClosedBiPipeline>(pipeline)
	{
		@PublicApi
		override fun createPipeline(): ClosedBiPipeline = PlaceholderBiLayer + createLayer().toOpenBiPipeline() + PlaceholderBiLayer
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
		entry?.handler?.invoke(exception)
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

	//@PublicApi
	//override fun toOpenUniPipeline(): OpenUniPipeline<I, O> = super<TransitiveLayerW>.toOpenUniPipeline()
}
