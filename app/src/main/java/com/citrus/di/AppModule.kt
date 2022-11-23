package com.citrus.di


import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.viewbinding.BuildConfig
import com.citrus.citrusac.present.current.adapter.CurrentAcAdapter
import com.citrus.citrusac.present.history.adapter.HistoryAcAdapter
import com.citrus.remote.ApiService
import com.citrus.util.Constants
import com.citrus.util.Prefs
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    companion object {
        private const val DEFAULT_CONNECT_TIME = 10L
        private const val DEFAULT_WRITE_TIME = 20L
        private const val DEFAULT_READ_TIME = 20L

        @Provides
        @Singleton
        fun okHttpClient(): OkHttpClient {
            val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

            if (BuildConfig.DEBUG) {
                val loggingInterceptor =
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                okHttpClientBuilder.addInterceptor(loggingInterceptor)
            }

            return okHttpClientBuilder
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(retrofit: Retrofit): ApiService =
            retrofit.create(ApiService::class.java)

        @Provides
        @Singleton
        fun providePref(application: Application): Prefs =
            Prefs(application)

        @Provides
        @Singleton
        fun provideSharedPreference(application: Application): SharedPreferences =
            application.getSharedPreferences(
                Constants.SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE
            )

    }

}


@EntryPoint
@InstallIn(SingletonComponent::class)
interface SpEntryPoint {
    fun sharedPreference(): SharedPreferences
}


@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {
    @Provides
    fun provideCurrentAcAdapter() =
        CurrentAcAdapter()

    @Provides
    fun provideHistoryAcAdapter() =
        HistoryAcAdapter()

}



