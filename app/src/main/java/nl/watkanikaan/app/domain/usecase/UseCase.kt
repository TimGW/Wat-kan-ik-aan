package nl.watkanikaan.app.domain.usecase

import nl.watkanikaan.app.domain.model.Result

interface UseCase<in Params, out T> {
    fun execute(params: Params): T
}

fun <In, Out> Result<In>.map(data: Out): Result<Out> = when (this) {
    is Result.Success<In> -> Result.Success(data)
    is Result.Error<In> -> Result.Error(error, data)
    is Result.Loading<In> -> Result.Loading(data)
}

