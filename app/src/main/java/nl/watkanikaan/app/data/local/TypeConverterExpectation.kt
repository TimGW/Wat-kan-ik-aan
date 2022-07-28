package nl.watkanikaan.app.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import nl.watkanikaan.app.data.model.WeatherEntity
import nl.watkanikaan.app.domain.model.Weather

@ProvidedTypeConverter
class TypeConverterForecast(private val moshi: Moshi) {
    private val mapType = Types.newParameterizedType(
        Map::class.java,
        Weather.Day::class.java,
        WeatherEntity.Forecast::class.java
    )

    @TypeConverter
    fun fromWeatherForecastJson(
        value: String
    ): Map<Weather.Day, WeatherEntity.Forecast>? {
        val adapter = moshi.adapter<Map<Weather.Day, WeatherEntity.Forecast>>(mapType)
        return if (value.isEmpty()) null else adapter.fromJson(value)
    }

    @TypeConverter
    fun toWeatherForecastJson(
        map: Map<Weather.Day, WeatherEntity.Forecast>
    ): String {
        val adapter = moshi.adapter<Map<Weather.Day, WeatherEntity.Forecast>>(mapType)
        return adapter.toJson(map)
    }
}