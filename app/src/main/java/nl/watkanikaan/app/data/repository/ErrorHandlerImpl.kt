package nl.watkanikaan.app.data.repository

import nl.watkanikaan.app.domain.repository.ErrorHandler
import nl.watkanikaan.app.domain.model.Result
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ErrorHandlerImpl @Inject constructor() : ErrorHandler {

    override fun getError(throwable: Throwable): Result.ErrorType {
        return when (throwable) {
            is IOException -> Result.ErrorType.IOError(throwable)
            is HttpException -> Result.ErrorType.HttpError(throwable, throwable.code())
            else -> Result.ErrorType.Unknown(throwable)
        }
    }

    override fun getApiError(statusCode: Int, throwable: Throwable?): Result.ErrorType {
        return Result.ErrorType.HttpError(throwable, statusCode)
    }
}