package nl.watkanikaan.app.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import nl.watkanikaan.app.domain.model.DefaultDispatcher
import nl.watkanikaan.app.domain.model.Profile
import nl.watkanikaan.app.domain.model.Recommendation
import nl.watkanikaan.app.domain.model.Weather
import javax.inject.Inject

class CalcRecommendationUseCase @Inject constructor(
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : UseCase<CalcRecommendationUseCase.Params, Flow<Recommendation>> {

    data class Params(
        val weather: Weather.Forecast,
        val profile: Profile = Profile()
    )

    override fun execute(
        params: Params
    ) = flow {
        val profile = params.profile
//        val expectation = params.weather.forecast[params.selected]
        val expectation = params.weather
        val isRaining = expectation.isRainExpected()

        val weather = params.weather
        val temp = profile.thermoception.actuarialTemperature(weather.temperature)

        val recommendation = createRecommendation(
            temp,
            weather.dewPoint,
            expectation.chanceOfSun,
            isRaining,
            weather.windForce
        )

        emit(recommendation)
    }.flowOn(dispatcher)

    private fun createRecommendation(
        temp: Double,
        dewPoint: Int?,
        chanceOfSun: Int,
        isRainExpected: Boolean,
        windForce: Int,
    ) = Recommendation(
        determineJacket(temp, dewPoint),
        determineTop(temp),
        determineBottom(temp, isRainExpected),
        determineExtras(temp, chanceOfSun, isRainExpected, windForce)
    )

    //    Een gemiddeld mens ervaart een dauwpunt onder 10 graden als niet benauwd of zwoel.
//    Ligt het dauwpunt rond 15 graden, dan wordt dat door velen ervaren als ‘een beetje benauwd’,
//    zeker tijdens inspanning. Dauwpunten tussen 15 en 20 graden kunnen al als matig benauwd worden
//    beschouwd en dauwpunten boven 20 graden als ‘erg benauwd’ of ‘zeer benauwd’.
//    Dauwpunten boven 26 graden leveren extreem benauwd weer op en kunnen gevaarlijk zijn.
    private fun determineJacket(
        temp: Double,
        dewPoint: Int? = null,
    ) = when {
        temp >= 20f -> Recommendation.Jacket.NONE
        temp >= 15f -> Recommendation.Jacket.SUMMER
        temp >= 10f -> Recommendation.Jacket.NORMAL
        else -> Recommendation.Jacket.WINTER
    }

    private fun determineTop(
        temp: Double,
    ) = when {
        temp >= 15.0 -> Recommendation.Top.T_SHIRT
        temp >= 10.0 -> Recommendation.Top.VEST
        else -> Recommendation.Top.SWEATER
    }

    private fun determineBottom(
        temp: Double,
        isRainExpected: Boolean,
    ) = if (temp >= 20.0 && !isRainExpected) {
        Recommendation.Bottom.SHORTS
    } else {
        Recommendation.Bottom.LONG
    }

    private fun determineExtras(
        temp: Double,
        chanceOfSun: Int,
        isRainExpected: Boolean,
        windForce: Int,
    ): Set<Recommendation.Extra> {
        val result = mutableSetOf<Recommendation.Extra>()

        if (chanceOfSun >= 70) result.add(Recommendation.Extra.SUNNY)
        if (temp <= 5.0) result.add(Recommendation.Extra.FREEZING)
        if (isRainExpected) {
            if (windForce >= 5) {
                result.add(Recommendation.Extra.RAIN_WINDY)
            } else {
                result.add(Recommendation.Extra.RAIN)
            }
        }

        return result
    }

    private fun Profile.Thermoception.actuarialTemperature(
        temperature: Double
    ): Double = when (this) {
        Profile.Thermoception.Cold -> temperature - 2.0
        Profile.Thermoception.Normal -> temperature
        Profile.Thermoception.Warm -> temperature + 2.0
    }

    private fun Weather.Forecast.isRainExpected() = chanceOfPrecipitation >= 40
}
