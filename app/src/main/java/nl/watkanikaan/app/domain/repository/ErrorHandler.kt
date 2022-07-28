package nl.watkanikaan.app.domain.repository

import nl.watkanikaan.app.domain.model.Result

interface ErrorHandler {
    fun getError(throwable: Throwable): Result.ErrorType
    fun getApiError(statusCode: Int, throwable: Throwable? = null): Result.ErrorType
}