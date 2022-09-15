package com.example.randomuselessfacts.repo

import androidx.lifecycle.LiveData
import com.example.randomuselessfacts.model.Fact
import retrofit2.Response

interface Repository {
    suspend fun getRandomFact(): Response<Fact>
    suspend fun getDailyFact(): Response<Fact>
    suspend fun saveFact(fact: Fact)
    fun readFacts(): LiveData<List<Fact>>
    suspend fun deleteFact(fact: Fact)
}