package com.example.randomuselessfacts.repo

import com.example.randomuselessfacts.DummyData.getDummyFact
import com.example.randomuselessfacts.model.Fact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

class FakeRepository : Repository {

    var errorResponse: Boolean = false

    private val savedFacts: MutableList<Fact> = mutableListOf()
    private val savedFactsStateFlow: MutableStateFlow<List<Fact>> = MutableStateFlow(savedFacts)

    override suspend fun getRandomFact(): Response<Fact> {
        return if (!errorResponse) Response.success(getDummyFact())
        else throw HttpException(
            Response.error<Fact>(
                400,
                "error message".toResponseBody()
            )
        )
    }

    override suspend fun getDailyFact(): Response<Fact> = Response.success(getDummyFact(null))

    override suspend fun saveFact(fact: Fact) {
        savedFacts.add(fact)
        savedFactsStateFlow.emit(savedFacts)
    }

    override fun readFacts(): Flow<List<Fact>> = savedFactsStateFlow

    override suspend fun deleteFact(fact: Fact) {
        savedFacts.remove(fact)
        savedFactsStateFlow.emit(savedFacts)
    }
}