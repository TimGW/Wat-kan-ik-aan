package nl.watkanikaan.app.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import nl.watkanikaan.app.domain.model.DefaultDispatcher
import nl.watkanikaan.app.domain.model.Profile
import nl.watkanikaan.app.domain.model.Recommendation
import nl.watkanikaan.app.domain.model.Recommendation.Bottom
import nl.watkanikaan.app.domain.model.Recommendation.Extra
import nl.watkanikaan.app.domain.model.Recommendation.Jacket
import nl.watkanikaan.app.domain.model.Recommendation.Top
import nl.watkanikaan.app.domain.model.Weather
import javax.inject.Inject

class CalcRecommendationUseCase @Inject constructor(
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : UseCase<CalcRecommendationUseCase.Params, Flow<Recommendation>> {

    data class Params(
        val selectedDay: Weather.Day,
        val weather: Weather.Forecast,
        val profile: Profile
    )

    override fun execute(
        params: Params
    ) = flow {
        emit(createRecommendation(params.selectedDay, params.weather, params.profile))
    }.flowOn(dispatcher)

    private fun createRecommendation(
        day: Weather.Day,
        weather: Weather.Forecast,
        profile: Profile,
    ): Recommendation {
        val temp = profile.actuarialTemperature(weather.temperature)
        val isRainExpected = weather.isRainExpected()

        return Recommendation(
            day,
            determineJacket(temp, weather.dewPoint),
            determineTop(temp),
            determineBottom(temp, isRainExpected, weather.dewPoint),
            determineExtras(temp, weather.chanceOfSun, isRainExpected, weather.windForce, weather.dewPoint)
        )
    }

    private fun determineJacket(
        temp: Double,
        dewPoint: Int? = null,
    ): Jacket? = when {
        temp >= 20.0 -> null
        temp >= 15.0 -> if (dewPoint != null && dewPoint >= 15) null else Jacket.SUMMER
        temp >= 10.0 -> Jacket.NORMAL
        else -> Jacket.WINTER
    }

    private fun determineTop(
        temp: Double,
    ): Top = when {
        temp >= 15.0 -> Top.T_SHIRT
        temp >= 10.0 -> Top.VEST
        else -> Top.SWEATER
    }

    private fun determineBottom(
        temp: Double,
        isRainExpected: Boolean,
        dewPoint: Int?,
    ): Bottom = when {
        temp >= 20.0 && !isRainExpected -> Bottom.SHORTS
        temp >= 15.0 -> if (dewPoint != null && dewPoint >= 15) Bottom.SHORTS else Bottom.LONG
        else -> Bottom.LONG
    }

    private fun determineExtras(
        temp: Double,
        chanceOfSun: Int,
        isRainExpected: Boolean,
        windForce: Int,
        dewPoint: Int?,
    ): Set<Extra> {
        val result = mutableSetOf<Extra>()

        dewPoint?.let { if (it >= 20) result.add(Extra.MUGGY) }
        if (chanceOfSun >= 70) result.add(Extra.SUNNY)
        if (temp <= 5.0) result.add(Extra.FREEZING)
        if (isRainExpected) if (windForce >= 5) result.add(Extra.RAIN_WINDY) else result.add(Extra.RAIN)

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
