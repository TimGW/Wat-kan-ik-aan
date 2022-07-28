package nl.watkanikaan.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import nl.watkanikaan.app.data.model.WeatherEntity

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = true)
@TypeConverters(TypeConverterForecast::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}
