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
		fun <I, O> builder(pipeline: OpenUniPipeline<I, O>): UniPipelineBuilder<I, O, OpenUniPipeline<I, O?>> =
				OpenUniPipelineBuilder(pipeline)

		@PublicApi
		fun <O> builder(pipeline: LeftClosedUniPipeline<O, *>): UniPipelineBuilder<Unit, O, LeftClosedUniPipeline<O?, *>> =
				LeftClosedUniPipelineBuilder(pipeline)

		@PublicApi
		fun <I> builder(pipeline: RightClosedUniPipeline<I, *>): UniPipelineBuilder<I, Unit, RightClosedUniPipeline<I, *>> =
				RightClosedUniPipelineBuilder(pipeline)

		@PublicApi
		fun builder(pipeline: ClosedUniPipeline): UniPipelineBuilder<Unit, Unit, ClosedUniPipeline> =
				ClosedUniPipelineBuilder(pipeline)
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

		protected abstract fun createLayer(): ExceptionCatcher
	}

	@PublicApi
	abstract class UniPipelineBuilder<I, O, P : UniPipeline<I, O?>> protected constructor(
			private val pipeline: UniPipeline<I, O>) : Builder()
	{
		@PublicApi
		abstract fun createPipeline(): P

		override fun createLayer() = UniPipelineExceptionCatcher(pipeline, exceptions)
	}

	private class OpenUniPipelineBuilder<I, O> @PublishedApi internal constructor(pipeline: UniPipeline<I, O>) :
			UniPipelineBuilder<I, O, OpenUniPipeline<I, O?>>(pipeline)
	{
		@PublicApi
		override fun createPipeline(): OpenUniPipeline<I, O> = createLayer().toOpenUniPipeline()
	}

	private class LeftClosedUniPipelineBuilder<O> @PublishedApi internal constructor(pipeline: LeftClosedUniPipeline<O, *>) :
			UniPipelineBuilder<Unit, O, LeftClosedUniPipeline<O, *>>(pipeline)
	{
		@PublicApi
		override fun createPipeline(): LeftClosedUniPipeline<O, Unit> = PlaceholderLayer + createLayer().toOpenUniPipeline()
	}

	private class RightClosedUniPipelineBuilder<I> @PublishedApi internal constructor(pipeline: RightClosedUniPipeline<I, *>) :
			UniPipelineBuilder<I, Unit, RightClosedUniPipeline<I, *>>(pipeline)
	{
		@PublicApi
		override fun createPipeline(): RightClosedUniPipeline<I, *> = createLayer().toOpenUniPipeline() + PlaceholderLayer
	}

	private class ClosedUniPipelineBuilder @PublishedApi internal constructor(pipeline: ClosedUniPipeline) :
			UniPipelineBuilder<Unit, Unit, ClosedUniPipeline>(pipeline)
	{
		@PublicApi
		override fun createPipeline(): ClosedUniPipeline = PlaceholderLayer + createLayer().toOpenUniPipeline() + PlaceholderLayer
	}

	private object PlaceholderLayer : CreatorLayer<Unit>, ConsumerLayer<Unit>
	{
		override fun transform(input: Unit) { }
	}

	protected inline fun <reified E : Exception> handleException(exception: E)
	{
		val entry = exceptions.firstOrNull { E::class.isSubclassOf(it.klass) } as ExceptionEntry<E>?
		entry?.handler?.invoke(exception)
	}

	private class UniPipelineExceptionCatcher<I, O> internal constructor(private val pipeline: UniPipeline<I, O>,
	                                                                       exceptions: List<ExceptionEntry<*>>) :
			ExceptionCatcher(exceptions), TransitiveLayer<I, O?>
	{
		@PublicApi
		override fun transform(input: I): O? = try
		{
			pipeline.transform(input)
		}
		catch(e: Exception)
		{
			handleException(e)
			null
		}
	}
}



private class BiPipelineExceptionCatcher<I, O> internal constructor(private val pipeline: BiPipeline<I, O>,
                                                                    exceptions: List<ExceptionEntry<*>>) :
		ExceptionCatcher(exceptions), TransitiveLayer<I?, O?>, TransitiveBiLayer<I?, O?>
{
	@PublicApi
	override fun transform(input: I?): O? = try
	{
		pipeline.transform(input)
	}
	catch(e: Exception)
	{
		handleException(e)
		null
	}

	@PublicApi
	override fun transformBack(input: O?): I? = try
	{
		pipeline.transformBack(input)
	}
	catch(e: Exception)
	{
		handleException(e)
		null
	}

	@PublicApi
	override fun toOpenUniPipeline(): OpenUniPipeline<I?, O?> = super<TransitiveLayer>.toOpenUniPipeline()
}
