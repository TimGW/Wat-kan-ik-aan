package nl.watkanikaan.app.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import nl.watkanikaan.app.data.local.SharedPref
import nl.watkanikaan.app.domain.model.DefaultDispatcher
import nl.watkanikaan.app.domain.model.Movement
import nl.watkanikaan.app.domain.model.Profile
import nl.watkanikaan.app.domain.model.Recommendation
import nl.watkanikaan.app.domain.model.Recommendation.Bottom
import nl.watkanikaan.app.domain.model.Recommendation.Extra
import nl.watkanikaan.app.domain.model.Recommendation.Jacket
import nl.watkanikaan.app.domain.model.Recommendation.Top
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.domain.model.Weather.Day
import nl.watkanikaan.app.domain.usecase.marker.CalcRecommendationUseCase
import java.time.LocalTime
import javax.inject.Inject

class CalcRecommendationUseCaseImpl @Inject constructor(
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    private val sharedPrefs: SharedPref,
) : CalcRecommendationUseCase {

    data class Params(
        val forecast: Weather.Forecast,
        val movement: Movement
    )

    override fun execute(
        params: Params
    ) = flow {
        emit(createRecommendation(params.forecast, params.movement))
    }.flowOn(dispatcher)

    private fun createRecommendation(
        forecast: Weather.Forecast,
        movement: Movement
    ): Recommendation {
        val temp = actuarialTemperature(sharedPrefs.getProfile(), forecast, movement)

        return Recommendation(
            selectedDay = forecast.day,
            jacket = determineJacket(temp),
            top = determineTop(temp),
            bottom = determineBottom(temp, forecast),
            extras = determineExtras(temp, forecast)
        )
    }

    private fun determineJacket(
        temp: Double,
    ): Jacket? = when {
        temp >= 20.0 -> null
        temp >= 15.0 -> Jacket.SUMMER
        temp >= 10.0 -> Jacket.NORMAL
        else -> Jacket.WINTER
    }

    private fun determineTop(
        temp: Double,
    ): Top = when {
        temp >= 20.0 -> Top.T_SHIRT
        temp >= 15.0 -> Top.VEST
        else -> Top.SWEATER
    }

    private fun determineBottom(
        temp: Double,
        forecast: Weather.Forecast,
    ): Bottom = when {
        forecast.isPrecipitationExpected() -> Bottom.LONG
        temp >= 21.0 -> Bottom.SHORTS
        else -> Bottom.LONG
    }

    private fun determineExtras(
        temp: Double,
        forecast: Weather.Forecast,
    ): Set<Extra> {
        val result = mutableSetOf<Extra>()

        if (forecast.isSunny()) {
            when (forecast.day) {
                Day.NOW, Day.TODAY -> {
                    val isSunUp = LocalTime.now().hour in forecast.sunUp..forecast.sunUnder - 2
                    if (isSunUp) result.add(Extra.SUNNY)
                }
                else -> result.add(Extra.SUNNY)
            }
        }
        if (temp <= 5.0) result.add(Extra.FREEZING)
        if (forecast.isPrecipitationExpected()) {
            if (forecast.windForce >= 5) result.add(Extra.RAIN_WINDY) else result.add(Extra.RAIN)
        }

        return result
    }
}

@Suppress("UNUSED_EXPRESSION")
fun actuarialTemperature(
    profile: Profile,
    forecast: Weather.Forecast,
    movement: Movement
): Double {
    var addition = 0.0

    when (profile.thermoception) {
        Profile.Thermoception.Cold -> addition -= 3
        Profile.Thermoception.Normal -> addition
        Profile.Thermoception.Warm -> addition += 3
    }
    when (movement) {
        Movement.Light -> addition += 5.0
        Movement.Heavy -> addition += 10.0
        else -> addition
    }
    if (forecast.isSunny()) addition += 5.0

    return forecast.windChillTemp + addition
}

fun Weather.Forecast.isSunny() =
    (weatherIcon == "zonnig" || weatherIcon == "halfbewolkt") && chanceOfSun >= 80

fun Weather.Forecast.isPrecipitationExpected(): Boolean {
    return chanceOfPrecipitation >= 60 || weatherIcon.contains("regen") ||
            weatherIcon == "buien" || weatherIcon == "hagel" || weatherIcon == "sneeuw"
}
