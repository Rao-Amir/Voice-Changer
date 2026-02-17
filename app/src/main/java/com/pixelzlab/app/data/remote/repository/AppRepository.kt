package com.pixelzlab.app.data.remote.repository

import com.pixelzlab.app.core.network.flowTransform
import com.pixelzlab.app.data.remote.api.ApiService
import com.pixelzlab.app.model.entity.Pokemon
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by pixelzlab on 17/09/2021.
 */

interface AppRepository {
    fun fetchPokemons(): Flow<List<Pokemon>>
}

class DefaultAppRepository @Inject constructor(
    private val apiService: ApiService,
) : AppRepository {

    override fun fetchPokemons(): Flow<List<Pokemon>> = flowTransform {
        apiService.fetchPokemons().data.orEmpty()
    }
}
