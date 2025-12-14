package edu.stanford.spezi.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Qualifier

/**
 * An interface serving as a namespace containing qualifier annotations for different [CoroutineDispatcher]s.
 *
 * This namespace defines qualifier annotations for different coroutine dispatchers,
 * to simplify injection by distinguishing between the main, default, IO, and unconfined dispatchers.
 * These annotations are used to inject either the corresponding dispatcher or coroutine scope with the same context.
 */
interface Dispatching {

    /**
     * Qualifier annotation for the main [CoroutineDispatcher].
     *
     * This annotation is used to indicate that the main dispatcher should be injected,
     * or to inject a coroutine scope with main dispatcher as coroutine context.
     */
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Main

    /**
     * Qualifier annotation for the default [CoroutineDispatcher].
     *
     * This annotation is used to indicate that the default dispatcher should be injected,
     * or to inject a coroutine scope with default dispatcher as coroutine context.
     */
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Default

    /**
     * Qualifier annotation for the IO [CoroutineDispatcher].
     *
     * This annotation is used to indicate that the IO dispatcher should be injected,
     * or to inject a coroutine scope with IO dispatcher as coroutine context.
     */
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class IO

    /**
     * Qualifier annotation for the unconfined [CoroutineDispatcher].
     *
     * This annotation is used to indicate that the unconfined dispatcher should be injected,
     * or to inject a coroutine scope with unconfined dispatcher as coroutine context.
     */
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Unconfined
}
