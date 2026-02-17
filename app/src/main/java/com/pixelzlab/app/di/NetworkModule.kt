package com.pixelzlab.app.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.pixelzlab.app.BuildConfig
import com.pixelzlab.app.data.local.datastore.PreferenceDataStore
import com.pixelzlab.app.data.remote.api.ApiService
import com.pixelzlab.app.data.remote.api.middleware.DefaultInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by pixelzlab on 15/09/2022.
 */

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideChuckerInterceptor(
        @ApplicationContext context: Context
    ): ChuckerInterceptor {
        val chuckerCollector = ChuckerCollector(
            context = context,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        return ChuckerInterceptor.Builder(context)
            .collector(chuckerCollector)
            .alwaysReadResponseBody(true)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        @ApplicationContext context: Context,
        moshi: Moshi,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        interceptor: Interceptor,
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder().apply {
            cache(
                Cache(
                    directory = File(context.cacheDir, "http_cache"),
                    maxSize = CACHE_SIZE
                )
            )
            addInterceptor(interceptor)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                addInterceptor(logging)
                addInterceptor(chuckerInterceptor)
                readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            }
        }
        return httpClientBuilder.build()
    }

    @Singleton
    @Provides
    fun provideInterceptor(preferenceDataStore: PreferenceDataStore): Interceptor {
        return DefaultInterceptor(preferenceDataStore)
    }

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    private const val READ_TIMEOUT: Long = 180
    private const val WRITE_TIMEOUT: Long = 180
    private const val CONNECTION_TIMEOUT: Long = 180
    private const val CACHE_SIZE = 50L * 1024L * 1024L // 50 MiB
}
