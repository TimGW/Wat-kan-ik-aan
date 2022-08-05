package nl.watkanikaan.app.data.repository

import nl.watkanikaan.app.domain.model.Result

interface ErrorHandler {
    fun getError(throwable: Throwable): Result.ErrorType
    fun getApiError(statusCode: Int, throwable: Throwable? = null): Result.ErrorType
}