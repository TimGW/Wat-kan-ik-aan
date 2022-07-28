package nl.watkanikaan.app.data.local

import androidx.room.*
import nl.watkanikaan.app.data.model.WeatherEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
abstract class WeatherDao {

    @Query("SELECT * FROM WeatherEntity WHERE id = 0")
    abstract fun getWeather(): Flow<WeatherEntity?>

    /**
     * SQLite database triggers only allow notifications at table level, not at row level.
     * distinctUntilChanged ensures that you only get notified when the row has changed
     */
    fun getWeatherDistinctUntilChanged() = getWeather().distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertWeather(weatherEntity: WeatherEntity)

    fun insertWithTimestamp(weatherEntity: WeatherEntity) {
        insertWeather(
            weatherEntity.copy(
                modifiedAt = System.currentTimeMillis()
            )
        )
    }
}
