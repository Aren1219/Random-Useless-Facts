package com.example.randomuselessfacts.repo

import com.example.randomuselessfacts.DummyData.getDummyFact
import com.example.randomuselessfacts.model.Fact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeRepository : Repository {

    var errorResponse: Boolean = false

    private val savedFactsFlow: Flow<List<Fact>> = flow {
        emit(savedFacts)
    }
    private val savedFacts: MutableList<Fact> = mutableListOf()

    override suspend fun getRandomFact(): Response<Fact> {
        return if (!errorResponse) Response.success(getDummyFact())
        else {
            val errorResponse =
                "{\n" +
                        "  \"type\": \"error\",\n" +
                        "}"
            val errorResponseBody =
                errorResponse.toResponseBody("application/json".toMediaTypeOrNull())
            Response.error(400, errorResponseBody)
        }
    }

    override suspend fun getDailyFact(): Response<Fact> = Response.success(getDummyFact(null))

    override suspend fun saveFact(fact: Fact) {
        savedFacts.add(fact)
    }

    override fun readFacts(): Flow<List<Fact>> = savedFactsFlow

    override suspend fun deleteFact(fact: Fact) {
        savedFacts.remove(fact)
    }
}