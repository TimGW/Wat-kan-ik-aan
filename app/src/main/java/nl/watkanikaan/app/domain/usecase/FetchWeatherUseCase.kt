package nl.watkanikaan.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import nl.watkanikaan.app.domain.model.Result
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.domain.repository.WeatherRepository
import javax.inject.Inject

class FetchWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository,
) : UseCase<FetchWeatherUseCase.Params, Flow<@JvmSuppressWildcards Result<Weather?>>> {

    data class Params(
        val lat: Double? = null,
        val long: Double? = null,
        val forceRefresh: Boolean? = null
    )

    override fun execute(
        params: Params
    ): Flow<Result<Weather?>> = repository.fetchWeather(
        params.lat, params.long, params.forceRefresh
    )
//        .map { response ->
//        return@map response.map(response.data)
//    }
}