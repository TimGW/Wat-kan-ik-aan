package nl.watkanikaan.app.presentation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import nl.watkanikaan.app.domain.usecase.CalcRecommendationUseCaseImpl
import nl.watkanikaan.app.domain.usecase.FetchWeatherUseCaseImpl
import nl.watkanikaan.app.domain.usecase.UpdateLocationUseCaseImpl
import nl.watkanikaan.app.domain.usecase.marker.CalcRecommendationUseCase
import nl.watkanikaan.app.domain.usecase.marker.FetchWeatherUseCase
import nl.watkanikaan.app.domain.usecase.marker.UpdateLocationUseCase

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {

    @Binds
    @ViewModelScoped
    abstract fun provideFetchWeatherUseCase(
        fetchWeatherUseCaseImpl: FetchWeatherUseCaseImpl
    ): FetchWeatherUseCase

    @Binds
    @ViewModelScoped
    abstract fun provideCalcRecommendationUseCase(
        calcRecommendationUseCaseImpl: CalcRecommendationUseCaseImpl
    ): CalcRecommendationUseCase

    @Binds
    @ViewModelScoped
    abstract fun provideUpdateLocationUseCase(
        updateLocationUseCaseImpl: UpdateLocationUseCaseImpl
    ): UpdateLocationUseCase
}