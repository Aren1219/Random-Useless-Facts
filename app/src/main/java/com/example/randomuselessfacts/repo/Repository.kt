package com.example.randomuselessfacts.repo

import com.example.randomuselessfacts.model.Fact
import retrofit2.Response

interface Repository {
    suspend fun getRandomFact(): Response<Fact>
    suspend fun getDailyFact(): Response<Fact>
}