package nl.watkanikaan.app.domain.usecase.marker

import kotlinx.coroutines.flow.Flow
import nl.watkanikaan.app.domain.model.Result
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.domain.usecase.FetchWeatherUseCaseImpl
import nl.watkanikaan.app.domain.usecase.UseCase

interface FetchWeatherUseCase :
    UseCase<FetchWeatherUseCaseImpl.Params, @JvmSuppressWildcards Flow<Result<Weather?>>>