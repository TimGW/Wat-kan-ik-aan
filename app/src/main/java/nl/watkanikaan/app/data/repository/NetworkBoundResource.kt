package nl.watkanikaan.app.data.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import nl.watkanikaan.app.domain.model.Result
import retrofit2.Response

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 * Adapted from: Guide to app architecture
 * https://developer.android.com/jetpack/guide
 *
 * @param <ResultType> Represents the domain model
 * @param <RequestType> Represents the (converted) network > database model
 */
abstract class NetworkBoundResource<RequestType, ResultType>(
    private val errorHandler: ErrorHandler,
) {
    fun asFlow(networkDispatcher: CoroutineDispatcher) = flow {
        val cachedData = fetchFromLocal().firstOrNull()

        try {
            if (shouldFetch(cachedData)) {
                emit(Result.Loading(cachedData)) // update loading state with cached data
                delay(10000)

                val apiResponse = fetchFromRemote()
                val remoteResponse = apiResponse.body()

                if (apiResponse.isSuccessful && remoteResponse != null) {
                    saveRemoteData(remoteResponse)

                    // Collects all the values from the given flow and emits them to the collector
                    emitAll(fetchFromLocal().map { Result.Success(it) })
                } else {
                    emitAll(fetchFromLocal().map {
                        Result.Error(errorHandler.getApiError(apiResponse.code()), it)
                    })
                }
            } else {
                emit(Result.Success(cachedData))
            }
        } catch (e: Exception) {
            emitAll(fetchFromLocal().map { Result.Error(errorHandler.getError(e), it) })
        }
    }.flowOn(networkDispatcher)

    @WorkerThread
    protected abstract suspend fun saveRemoteData(response: RequestType)

    @MainThread
    protected abstract fun fetchFromLocal(): Flow<ResultType>

    @MainThread
    protected abstract suspend fun fetchFromRemote(): Response<RequestType>

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean
}
