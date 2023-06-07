package com.eleks.data.network

import com.eleks.data.model.MemeDataModel
import retrofit2.http.GET

interface MemesApiService {

    @GET("/memes/random?min-raiting=7&media-type=image")
    suspend fun getRandomMeme(
    ): MemeDataModel
}
