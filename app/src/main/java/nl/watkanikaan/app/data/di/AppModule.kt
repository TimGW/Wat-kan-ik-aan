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
import nl.watkanikaan.app.BuildConfig
import nl.watkanikaan.app.data.local.AppDatabase
import nl.watkanikaan.app.data.local.DefaultSharedPrefs
import nl.watkanikaan.app.data.local.SharedPref
import nl.watkanikaan.app.data.local.TypeConverterForecast
import nl.watkanikaan.app.data.remote.WeatherJsonAdapter
import nl.watkanikaan.app.domain.model.MoshiDefault
import nl.watkanikaan.app.domain.model.MoshiNetwork
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindSharedPrefs(defaultSharedPrefs: DefaultSharedPrefs): SharedPref

    companion object {
        @Provides
        @Singleton
        fun provideClock(): Clock = Clock.systemDefaultZone()

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
        fun provideOkHttpClient(): OkHttpClient {
            val builder = OkHttpClient().newBuilder()

            if (BuildConfig.DEBUG) {
                builder.addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
            return builder.build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(
            okHttpClient: OkHttpClient,
            @MoshiNetwork moshi: Moshi,
        ): Retrofit = Retrofit.Builder().baseUrl("https://timgw.github.io/")
            .client(okHttpClient)
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