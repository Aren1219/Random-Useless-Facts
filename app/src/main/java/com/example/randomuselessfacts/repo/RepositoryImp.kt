package com.example.randomuselessfacts.repo

import androidx.lifecycle.LiveData
import com.example.randomuselessfacts.api.FactsApi
import com.example.randomuselessfacts.database.FactDao
import com.example.randomuselessfacts.model.Fact
import retrofit2.Response
import javax.inject.Inject

class RepositoryImp @Inject constructor(
    val factsApi: FactsApi,
    val factDao: FactDao
): Repository {

    override suspend fun getRandomFact(): Response<Fact> = factsApi.getRandomFact()

    override suspend fun getDailyFact(): Response<Fact> = factsApi.getDailyFact()

    override suspend fun saveFact(fact: Fact) {
        factDao.insertFact(fact)
    }

    override fun readFacts(): LiveData<List<Fact>> = factDao.getAllFacts()

    override suspend fun deleteFact(fact: Fact) {
        factDao.deleteFact(fact)
    }
}