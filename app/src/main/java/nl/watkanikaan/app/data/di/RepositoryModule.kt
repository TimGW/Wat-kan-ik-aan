package nl.watkanikaan.app.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nl.watkanikaan.app.data.repository.Mapper
import nl.watkanikaan.app.data.model.WeatherEntity
import nl.watkanikaan.app.data.repository.ErrorHandlerImpl
import nl.watkanikaan.app.data.repository.WeatherMapper
import nl.watkanikaan.app.data.repository.WeatherRepositoryImpl
import nl.watkanikaan.app.domain.model.Weather
import nl.watkanikaan.app.data.repository.ErrorHandler
import nl.watkanikaan.app.domain.repository.WeatherRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindWeatherMapper(
        mapper: WeatherMapper
    ): Mapper<WeatherEntity, Weather>

    @Binds
    abstract fun bindErrorHandler(
        errorHandlerImpl: ErrorHandlerImpl
    ): ErrorHandler

    @Binds
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository
}
