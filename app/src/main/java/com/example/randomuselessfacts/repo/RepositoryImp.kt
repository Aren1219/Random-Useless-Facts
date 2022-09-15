package com.example.randomuselessfacts.repo

import com.example.randomuselessfacts.api.FactsApi
import com.example.randomuselessfacts.model.Fact
import retrofit2.Response
import javax.inject.Inject

class RepositoryImp @Inject constructor(
    val factsApi: FactsApi
): Repository {
    override suspend fun getRandomFact(): Response<Fact> = factsApi.getRandomFact()
    override suspend fun getDailyFact(): Response<Fact> = factsApi.getDailyFact()
}