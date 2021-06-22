package com.mraha.imagesearchapp.api

import com.mraha.imagesearchapp.data.Photo
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UnsplashApi {

    companion object {
        const val BASE_URL="https://api.thecatapi.com/v1/images/";
    }

    @Headers("x-api-key: d579ab73-5984-4355-abeb-040872d9095f")
    @GET("search?order=Desc")
    suspend fun searchPhotos(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<Photo>
}