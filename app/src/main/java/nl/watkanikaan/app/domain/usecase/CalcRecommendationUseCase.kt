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
        val day: Weather.Day,
        val weather: Weather.Forecast,
        val profile: Profile
    )

    override fun execute(
        params: Params
    ) = flow {
        val profile = params.profile
        val day = params.day
        val expectation = params.weather
        val isRaining = expectation.isRainExpected()

        val weather = params.weather
        val temp = profile.actuarialTemperature(weather.temperature)

        val recommendation = createRecommendation(
            day,
            temp,
            weather.dewPoint,
            expectation.chanceOfSun,
            isRaining,
            weather.windForce
        )

        emit(recommendation)
    }.flowOn(dispatcher)

    private fun createRecommendation(
        day: Weather.Day,
        temp: Double,
        dewPoint: Int?,
        chanceOfSun: Int,
        isRainExpected: Boolean,
        windForce: Int,
    ) = Recommendation(
        day,
        determineJacket(temp, dewPoint),
        determineTop(temp),
        determineBottom(temp, isRainExpected, dewPoint),
        determineExtras(temp, chanceOfSun, isRainExpected, windForce, dewPoint)
    )

    private fun determineJacket(
        temp: Double,
        dewPoint: Int? = null,
    ) = when {
        temp >= 20f -> Recommendation.Jacket.NONE
        temp >= 15f -> {
            if (dewPoint != null && dewPoint >= 15) {
                Recommendation.Jacket.NONE
            } else {
                Recommendation.Jacket.SUMMER
            }
        }
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
        dewPoint: Int?,
    ) = when {
        temp >= 20.0 && !isRainExpected -> Recommendation.Bottom.SHORTS
        temp >= 15.0 -> if (dewPoint != null && dewPoint >= 15) {
            Recommendation.Bottom.SHORTS
        } else {
            Recommendation.Bottom.LONG
        }
        else -> Recommendation.Bottom.LONG
    }

    private fun determineExtras(
        temp: Double,
        chanceOfSun: Int,
        isRainExpected: Boolean,
        windForce: Int,
        dewPoint: Int?,
    ): Set<Recommendation.Extra> {
        val result = mutableSetOf<Recommendation.Extra>()

        dewPoint?.let { if (it >= 20) result.add(Recommendation.Extra.MUGGY) }
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

    private fun Profile.actuarialTemperature(
        temperature: Double
    ): Double {
        var result = when (thermoception) {
            Profile.Thermoception.Cold -> temperature - 2.0
            Profile.Thermoception.Normal -> temperature
            Profile.Thermoception.Warm -> temperature + 2.0
        }
        if (age >= 70) result -= 1.0
        if (gender is Profile.Gender.Woman) result -= 1.0

        return result
    }

    private fun Weather.Forecast.isRainExpected() = chanceOfPrecipitation >= 40
}
