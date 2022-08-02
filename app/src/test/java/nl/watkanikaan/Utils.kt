package nl.watkanikaan

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import nl.watkanikaan.app.domain.usecase.UseCase

@OptIn(ExperimentalCoroutinesApi::class)
fun <P, T> runUseCase(
    setup: (CoroutineDispatcher) -> UseCase<P, T>,
    block: suspend (UseCase<P, T>) -> Unit
) = runTest {
    val dispatcher = StandardTestDispatcher(testScheduler)
    val uc = setup.invoke(dispatcher)
    block.invoke(uc)
}

const val ANY_I = -1
const val ANY_D = -1.0
const val ANY_S = ""
