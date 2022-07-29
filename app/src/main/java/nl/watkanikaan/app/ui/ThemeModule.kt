package nl.watkanikaan.app.ui

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ThemeModule {

    @Binds
    abstract fun provideThemeHelper(themeHelperImpl: ThemeHelperImpl): ThemeHelper
}