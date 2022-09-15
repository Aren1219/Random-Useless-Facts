package com.example.randomuselessfacts.api

import com.example.randomuselessfacts.model.Fact
import retrofit2.Response
import retrofit2.http.GET

interface FactsApi {

    @GET(ApiReferences.END_POINT_RANDOM)
    suspend fun getRandomFact(): Response<Fact>

    @GET(ApiReferences.END_POINT_TODAY)
    suspend fun getDailyFact(): Response<Fact>
}