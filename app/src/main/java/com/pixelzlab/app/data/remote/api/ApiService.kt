package com.pixelzlab.app.data.remote.api

import com.pixelzlab.app.model.base.BaseResponse
import com.pixelzlab.app.model.entity.Pokemon
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by pixelzlab on 8/5/20.
 */
interface ApiService {

    @GET("pokemon")
    suspend fun fetchPokemons(
        @Query("limit") limit: Int = 20,
        @Query("offset") page: Int = 0
    ): BaseResponse<List<Pokemon>>

}
