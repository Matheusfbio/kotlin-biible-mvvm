package com.br.android_learn.data.api

import com.br.android_learn.data.model.BibleVerse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface BibleApiService {
    @GET("{passage}")
    suspend fun getVerse(
        @Path("passage") passage: String
    ): BibleVerse
}

object RetrofitInstance {
    val api: BibleApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://bible-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BibleApiService::class.java)
    }
}
