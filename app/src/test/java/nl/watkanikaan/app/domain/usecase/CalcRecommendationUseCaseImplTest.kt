package nl.watkanikaan.app.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import nl.watkanikaan.ANY_D
import nl.watkanikaan.ANY_I
import nl.watkanikaan.ANY_S
import nl.watkanikaan.app.data.local.SharedPref
import nl.watkanikaan.app.domain.model.Movement
import nl.watkanikaan.app.domain.model.Profile
import nl.watkanikaan.app.domain.model.Recommendation
import nl.watkanikaan.app.domain.model.Recommendation.Extra
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.runUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

class CalcRecommendationUseCaseImplTest {
    private val sharedPrefs: SharedPref = mock()
    private val localDate = LocalDate.of(2022, 8, 23)
    private val anyForecast =
        Weather.Forecast(
            Weather.Day.NOW,
            null,
            ANY_S,
            ANY_D,
            ANY_I,
            ANY_D,
            ANY_I,
            ANY_I,
            ANY_I,
            ANY_I
        )
    private val movement = Movement.Rest
    private val clock = Clock.fixed(
        localDate.atTime(LocalTime.NOON).toInstant(ZoneOffset.UTC),
        ZoneId.systemDefault()
    );
    private val setup: (CoroutineDispatcher) -> CalcRecommendationUseCaseImpl = {
        `when`(sharedPrefs.getProfile()).thenReturn(Profile())
        CalcRecommendationUseCaseImpl(it, sharedPrefs, clock)
    }

    @Test
    fun testJacket_above20degrees_nojacket() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 20.0)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val result = it.execute(params).first()

        assertNull(result.jacket)
    }

    @Test
    fun testJacket_above15degreesNoDewPoint_summerJacket() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 15.0)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val result = it.execute(params).first()

        assertEquals(Recommendation.Jacket.SUMMER, result.jacket)
    }

    @Test
    fun testJacket_above10degrees_normalJacket() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 10.0)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Jacket.NORMAL, actual.jacket)
    }

    @Test
    fun testJacket_under10degrees_winterJacket() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 9.9)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Jacket.WINTER, actual.jacket)
    }

    @Test
    fun testTop_under15degrees_sweater() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 14.9)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Top.SWEATER, actual.top)
    }

    @Test
    fun testTop_above15degrees_vest() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 15.0)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Top.VEST, actual.top)
    }

    @Test
    fun testTop_above20degrees_tshirt() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 20.0)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Top.T_SHIRT, actual.top)
    }

    @Test
    fun testBottom_under21degrees_long() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 20.9)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Bottom.LONG, actual.bottom)
    }

    @Test
    fun testBottom_above21degrees_short() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 21.0)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Bottom.SHORTS, actual.bottom)
    }

    @Test
    fun testBottom_above21degreesRaining_long() = runUseCase(setup) {
        val forecast = anyForecast.copy(chanceOfPrecipitation = 100, windChillTemp = 21.0)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Bottom.LONG, actual.bottom)
    }

    @Test
    fun testExtras_isSunny_showSunnyMessage() = runUseCase(setup) {
        val forecast = anyForecast.copy(chanceOfSun = 100, weatherIcon = "zonnig", sunUp = 8, sunUnder = 21)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertTrue(actual.extras.contains(Extra.SUNNY))
    }

    @Test
    fun testExtras_isFreezing_showFreezingMessage() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 5.0)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertTrue(actual.extras.contains(Extra.FREEZING))
    }

    @Test
    fun testExtras_isRainingHighWind_showWindyRainMessage() = runUseCase(setup) {
        val forecast = anyForecast.copy(chanceOfPrecipitation = 100, windForce = 5)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertTrue(actual.extras.contains(Extra.RAIN_WINDY))
    }

    @Test
    fun testExtras_isRainingLowWind_showRainMessage() = runUseCase(setup) {
        val forecast = anyForecast.copy(chanceOfPrecipitation = 100, windForce = 4)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertTrue(actual.extras.contains(Extra.RAIN))
    }

    @Test
    fun testExtras_allElements_showAllMessages() = runUseCase(setup) {
        val forecast = anyForecast.copy(
            chanceOfSun = 100,
            chanceOfPrecipitation = 100,
            windForce = 4,
        )
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertTrue(
            actual.extras.containsAll(
                listOf(
                    Extra.RAIN,
                    Extra.FREEZING,
                )
            )
        )
    }

    @Test
    fun testExtras_FreezingSunny_showAllMessages() = runUseCase(setup) {
        val forecast = anyForecast.copy(
            chanceOfSun = 100,
            weatherIcon = "zonnig",
            sunUp = 9,
            sunUnder = 21
        )
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertTrue(
            actual.extras.containsAll(
                listOf(
                    Extra.SUNNY,
                    Extra.FREEZING,
                )
            )
        )
    }

    @Test
    fun `testActuarialTemperature normal noDiff`() {
        val profile = Profile(Profile.Thermoception.Normal)
        val baselineTemp = 20.0

        assertEquals(
            baselineTemp,
            actuarialTemperature(profile, anyForecast.copy(windChillTemp = baselineTemp), movement),
            0.01
        )
    }

    @Test
    fun `testActuarialTemperature cold minus3`() {
        val profile = Profile(Profile.Thermoception.Cold)
        val baselineTemp = 20.0
        val expectedTemp = baselineTemp - 3.0

        assertEquals(
            expectedTemp,
            actuarialTemperature(profile, anyForecast.copy(windChillTemp = baselineTemp), movement),
            0.01
        )
    }

    @Test
    fun `testActuarialTemperature warm plus3`() {
        val profile = Profile(Profile.Thermoception.Warm)
        val baselineTemp = 20.0
        val expectedTemp = baselineTemp + 3

        assertEquals(
            expectedTemp,
            actuarialTemperature(profile, anyForecast.copy(windChillTemp = baselineTemp), movement),
            0.01
        )
    }

    @Test
    fun `testIsPrecipitationExpected 60percent true`() {
        val forecast = anyForecast.copy(chanceOfPrecipitation = 60)

        assertTrue(forecast.isPrecipitationExpected())
    }

    @Test
    fun `testIsPrecipitationExpected 59percent false`() {
        val forecast = anyForecast.copy(chanceOfPrecipitation = 59)

        assertFalse(forecast.isPrecipitationExpected())
    }

    @Test
    fun `testIsPrecipitationExpected 60percentRain true`() {
        val forecast = anyForecast.copy(weatherIcon = "regen", chanceOfPrecipitation = 60)

        assertTrue(forecast.isPrecipitationExpected())
    }

    @Test
    fun `testIsPrecipitationExpected 59percentRain true`() {
        val forecast = anyForecast.copy(weatherIcon = "regen", chanceOfPrecipitation = 59)

        assertTrue(forecast.isPrecipitationExpected())
    }

    @Test
    fun `testIsPrecipitationExpected 39percentSun false`() {
        val forecast = anyForecast.copy(weatherIcon = "sun", chanceOfPrecipitation = 39)

        assertFalse(forecast.isPrecipitationExpected())
    }

    @Test
    fun `testIsPrecipitationExpected 80percentSun true`() {
        val forecast = anyForecast.copy(weatherIcon = "sun", chanceOfPrecipitation = 80)

        assertTrue(forecast.isPrecipitationExpected())
    }
}
