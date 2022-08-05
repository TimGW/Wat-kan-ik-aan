package nl.watkanikaan.app.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import nl.watkanikaan.app.data.model.WeatherEntity
import nl.watkanikaan.app.domain.model.Weather

@ProvidedTypeConverter
class TypeConverterForecast(private val moshi: Moshi) {
    private val type = Types.newParameterizedType(
        List::class.java,
        WeatherEntity.Forecast::class.java
    )

    @TypeConverter
    fun fromWeatherForecastJson(
        value: String
    ): List<WeatherEntity.Forecast>? {
        val adapter = moshi.adapter<List<WeatherEntity.Forecast>>(type)
        return if (value.isEmpty()) null else adapter.fromJson(value)
    }

    @TypeConverter
    fun toWeatherForecastJson(
        map: List<WeatherEntity.Forecast>
    ): String {
        val adapter = moshi.adapter<List<WeatherEntity.Forecast>>(type)
        return adapter.toJson(map)
    }
}