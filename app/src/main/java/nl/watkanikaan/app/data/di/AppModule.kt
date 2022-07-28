package nl.watkanikaan.app.data.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nl.watkanikaan.app.data.local.AppDatabase
import nl.watkanikaan.app.data.local.TypeConverterForecast
import nl.watkanikaan.app.data.remote.WeatherJsonAdapter
import nl.watkanikaan.app.data.repository.ErrorHandlerImpl
import nl.watkanikaan.app.domain.model.MoshiDefault
import nl.watkanikaan.app.domain.model.MoshiNetwork
import nl.watkanikaan.app.domain.repository.ErrorHandler
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Module for all globally required dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindErrorHandler(errorHandlerImpl: ErrorHandlerImpl): ErrorHandler

    companion object {

        @Provides
        @Singleton
        fun providesRoomDb(
            @ApplicationContext context: Context,
            @MoshiDefault moshi: Moshi
        ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "database")
            .fallbackToDestructiveMigration()
            .addTypeConverter(TypeConverterForecast(moshi))
            .build()

        @Provides
        @Singleton
        fun provideRetrofit(
            @MoshiNetwork moshi: Moshi
        ): Retrofit = Retrofit.Builder().baseUrl("https://weerlive.nl/api/")
            .client(OkHttpClient().newBuilder().build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        @Provides
        @MoshiNetwork
        fun provideNetworkMoshi(
            builder: Moshi.Builder
        ): Moshi = builder
            .add(WeatherJsonAdapter())
            .build()

        @Provides
        @MoshiDefault
        fun provideDefaultMoshi(
            builder: Moshi.Builder
        ): Moshi = builder.build()

        @Provides
        fun provideMoshiBuilder() = Moshi.Builder()
    }
}