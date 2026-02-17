package com.pixelzlab.app.di

import com.pixelzlab.app.data.remote.api.ApiService
import com.pixelzlab.app.data.remote.repository.AppRepository
import com.pixelzlab.app.data.remote.repository.DefaultAppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by pixelzlab on 8/7/20.
 */

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAppRepository(
        apiService: ApiService,
    ): AppRepository {
        return DefaultAppRepository(apiService)
    }
}
