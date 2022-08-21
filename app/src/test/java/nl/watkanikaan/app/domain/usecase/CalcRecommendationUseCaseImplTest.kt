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
import java.time.LocalDate

class CalcRecommendationUseCaseImplTest {
    private val sharedPrefs: SharedPref = mock()
    private val anyForecast =
        Weather.Forecast(Weather.Day.NOW,null, ANY_S, ANY_D, ANY_I, ANY_D, ANY_I, ANY_I, ANY_I, ANY_I)
    private val movement = Movement.Rest
    private val setup: (CoroutineDispatcher) -> CalcRecommendationUseCaseImpl = {
        `when`(sharedPrefs.getProfile()).thenReturn(Profile())
        CalcRecommendationUseCaseImpl(it, sharedPrefs)
    }

    @Test
    fun testJacket_above20degrees_nojacket() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 20.0)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val result = it.execute(params).first()

        assertNull(result.jacket)
    }

    @Test
    fun testJacket_above15degreesHighDewPoint_noJacket() = runUseCase(setup) {
        val forecast = anyForecast.copy(dewPoint = 20, windChillTemp = 15.0)
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
    fun testTop_under10degrees_sweater() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 9.9)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Top.SWEATER, actual.top)
    }

    @Test
    fun testTop_above10degrees_vest() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 10.0)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Top.VEST, actual.top)
    }

    @Test
    fun testTop_above15degrees_tshirt() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 15.0)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Top.T_SHIRT, actual.top)
    }

    @Test
    fun testBottom_under15degrees_long() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 14.9)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Bottom.LONG, actual.bottom)
    }

    @Test
    fun testBottom_above15degreesMuggy_shorts() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 15.0, dewPoint = 20)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Bottom.SHORTS, actual.bottom)
    }

    @Test
    fun testBottom_above15degreesNotMuggy_long() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 15.0)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Bottom.LONG, actual.bottom)
    }

    @Test
    fun testBottom_above20degrees_short() = runUseCase(setup) {
        val forecast = anyForecast.copy(windChillTemp = 20.0)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Bottom.SHORTS, actual.bottom)
    }

    @Test
    fun testBottom_above20degreesRaining_long() = runUseCase(setup) {
        val forecast = anyForecast.copy(chanceOfPrecipitation = 100, windChillTemp = 20.0)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertEquals(Recommendation.Bottom.LONG, actual.bottom)
    }

    @Test
    fun testExtras_isMuggy_showMuggyMessage() = runUseCase(setup) {
        val forecast = anyForecast.copy(dewPoint = 20)
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertTrue(actual.extras.contains(Extra.MUGGY))
    }

    @Test
    fun testExtras_isSunny_showSunnyMessage() = runUseCase(setup) {
        val forecast = anyForecast.copy(chanceOfSun = 100)
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
            dewPoint = 20,
            chanceOfSun = 100,
            chanceOfPrecipitation = 100,
            windForce = 4,
        )
        val params = CalcRecommendationUseCaseImpl.Params(forecast, movement)

        val actual = it.execute(params).first()

        assertTrue(
            actual.extras.containsAll(
                listOf(
                    Extra.MUGGY,
                    Extra.SUNNY,
                    Extra.FREEZING,
                    Extra.RAIN
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
    fun `testActuarialTemperature cold minus2`() {
        val profile = Profile(Profile.Thermoception.Cold)
        val baselineTemp = 20.0
        val expectedTemp = baselineTemp - 2.5

        assertEquals(
            expectedTemp,
            actuarialTemperature(profile, anyForecast.copy(windChillTemp = baselineTemp), movement),
            0.01
        )
    }

    @Test
    fun `testActuarialTemperature warm plus2`() {
        val profile = Profile(Profile.Thermoception.Warm)
        val baselineTemp = 20.0
        val expectedTemp = baselineTemp + 2.5

        assertEquals(
            expectedTemp,
            actuarialTemperature(profile, anyForecast.copy(windChillTemp = baselineTemp), movement),
            0.01
        )
    }

    @Test
    fun `testIsPrecipitationExpected 40percent true`() {
        val forecast = anyForecast.copy(chanceOfPrecipitation = 40)

        assertTrue(forecast.isPrecipitationExpected())
    }

    @Test
    fun `testIsPrecipitationExpected 39percent false`() {
        val forecast = anyForecast.copy(chanceOfPrecipitation = 39)

        assertFalse(forecast.isPrecipitationExpected())
    }

    @Test
    fun `testIsPrecipitationExpected 40percentRain true`() {
        val forecast = anyForecast.copy(weatherIcon = "regen", chanceOfPrecipitation = 40)

        assertTrue(forecast.isPrecipitationExpected())
    }

    @Test
    fun `testIsPrecipitationExpected 39percentRain true`() {
        val forecast = anyForecast.copy(weatherIcon = "regen", chanceOfPrecipitation = 39)

        assertTrue(forecast.isPrecipitationExpected())
    }

    @Test
    fun `testIsPrecipitationExpected 39percentSun false`() {
        val forecast = anyForecast.copy(weatherIcon = "sun", chanceOfPrecipitation = 39)

        assertFalse(forecast.isPrecipitationExpected())
    }

    @Test
    fun `testIsPrecipitationExpected 40percentSun true`() {
        val forecast = anyForecast.copy(weatherIcon = "sun", chanceOfPrecipitation = 40)

        assertTrue(forecast.isPrecipitationExpected())
    }
}
