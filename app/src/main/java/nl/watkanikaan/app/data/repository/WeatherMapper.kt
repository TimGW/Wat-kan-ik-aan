package nl.watkanikaan.app.data.repository

import nl.watkanikaan.app.data.model.WeatherEntity
import nl.watkanikaan.app.domain.model.Weather
import javax.inject.Inject

class WeatherMapper @Inject constructor(): Mapper<WeatherEntity, Weather> {

    override fun mapOutgoing(domain: Weather): WeatherEntity {
        return WeatherEntity(
            modifiedAt = domain.modifiedAt,
            location = domain.location,
            dayExpectation = domain.dayExpectation,
            forecast = domain.forecast.map {
                WeatherEntity.Forecast(
                    it.day,
                    it.dewPoint,
                    it.weatherIcon,
                    it.windChillTemp,
                    it.windForce,
                    it.windSpeed,
                    it.chanceOfPrecipitation,
                    it.chanceOfSun,
                    it.sunUp,
                    it.sunUnder,
                )
            },
            weatherAlarm = domain.weatherAlarm,
        )
    }

    override fun mapIncoming(network: WeatherEntity): Weather {
        return Weather(
            modifiedAt = network.modifiedAt,
            location = network.location,
            dayExpectation = network.dayExpectation,
            forecast = network.forecast.map {
                Weather.Forecast(
                    it.day,
                    it.dewPoint,
                    it.weatherIcon,
                    it.temperature,
                    it.windForce,
                    it.windSpeed,
                    it.chanceOfPrecipitation,
                    it.chanceOfSun,
                    it.sunUp,
                    it.sunUnder,
                )
            },
            weatherAlarm = network.weatherAlarm,
        )
    }
}