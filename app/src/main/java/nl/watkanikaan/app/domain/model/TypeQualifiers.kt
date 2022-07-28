package nl.watkanikaan.app.domain.model

import javax.inject.Qualifier

/** Moshi builders */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MoshiNetwork

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MoshiDefault

/** Coroutines Dispatchers */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MainDispatcher
