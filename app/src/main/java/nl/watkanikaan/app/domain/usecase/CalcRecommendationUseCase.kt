package nl.watkanikaan.app.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import nl.watkanikaan.app.data.local.SharedPref
import nl.watkanikaan.app.domain.model.DefaultDispatcher
import nl.watkanikaan.app.domain.model.Profile
import nl.watkanikaan.app.domain.model.Recommendation
import nl.watkanikaan.app.domain.model.Recommendation.Bottom
import nl.watkanikaan.app.domain.model.Recommendation.Extra
import nl.watkanikaan.app.domain.model.Recommendation.Jacket
import nl.watkanikaan.app.domain.model.Recommendation.Top
import nl.watkanikaan.app.domain.model.Weather
import java.time.LocalDate
import javax.inject.Inject

class CalcRecommendationUseCase @Inject constructor(
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    private val sharedPrefs: SharedPref,
) : UseCase<CalcRecommendationUseCase.Params, Flow<Recommendation>> {

    data class Params(val forecast: Weather.Forecast)

    override fun execute(
        params: Params
    ) = flow {
        emit(createRecommendation(params.forecast))
    }.flowOn(dispatcher)

    private fun createRecommendation(
        forecast: Weather.Forecast,
    ): Recommendation {
        val temp = actuarialTemperature(sharedPrefs.getProfile(), forecast)

        return Recommendation(
            jacket = determineJacket(temp, forecast),
            top = determineTop(temp),
            bottom = determineBottom(temp, forecast),
            extras = determineExtras(temp, forecast)
        )
    }

    private fun determineJacket(
        temp: Double,
        forecast: Weather.Forecast,
    ): Jacket? = when {
        temp >= 20.0 -> null
        temp >= 15.0 -> if (forecast.dewPoint.isMuggy()) null else Jacket.SUMMER
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
        forecast: Weather.Forecast,
    ): Bottom = when {
        forecast.isPrecipitationExpected() -> Bottom.LONG
        temp >= 20.0 -> Bottom.SHORTS
        temp >= 15.0 -> if (forecast.dewPoint.isMuggy()) Bottom.SHORTS else Bottom.LONG
        else -> Bottom.LONG
    }

    private fun determineExtras(
        temp: Double,
        forecast: Weather.Forecast,
    ): Set<Extra> {
        val result = mutableSetOf<Extra>()

        if (forecast.dewPoint.isMuggy()) result.add(Extra.MUGGY)
        if (forecast.isSunny()) result.add(Extra.SUNNY)
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
    forecast: Weather.Forecast
): Double {
    var addition = 0.0

    when (profile.thermoception) {
        Profile.Thermoception.Cold -> addition -= 2.5
        Profile.Thermoception.Normal -> addition
        Profile.Thermoception.Warm -> addition += 2.5
    }
    when (profile.movement) {
        Profile.Movement.Rest -> addition += 0.0
        Profile.Movement.Light -> addition += 2.0
        Profile.Movement.Heavy -> addition += 4.0
    }
    if (profile.age >= 70) addition -= 2.0
    if (profile.gender is Profile.Gender.Female) addition -= 2.5
    if (forecast.isSunny()) addition += 5.0

    return forecast.windChillTemp + addition.coerceIn(-7.0, 7.0)
}

fun Weather.Forecast.isSunny() = chanceOfSun >= 65 || weatherIcon == "zonnig"

fun Int?.isMuggy() = this != null && this >= 20 && LocalDate.now().isWarmMonth()

fun Weather.Forecast.isPrecipitationExpected(): Boolean {
    return chanceOfPrecipitation >= 40 || weatherIcon.contains("regen") ||
            weatherIcon == "buien" || weatherIcon == "hagel" || weatherIcon == "sneeuw"
}

fun LocalDate.isWarmMonth() = monthValue in 5..9