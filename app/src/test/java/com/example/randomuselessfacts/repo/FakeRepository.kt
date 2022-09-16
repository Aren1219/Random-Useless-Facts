package com.example.randomuselessfacts.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.randomuselessfacts.DummyData.getDummyFact
import com.example.randomuselessfacts.model.Fact
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeRepository: Repository{

    var errorResponse: Boolean = false

    private val savedList: MutableList<Fact> = mutableListOf()
    private val _savedFacts: MutableLiveData<List<Fact>> = MutableLiveData(savedList)

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
        savedList.add(fact)
    }

    override fun readFacts(): LiveData<List<Fact>> = _savedFacts

    override suspend fun deleteFact(fact: Fact) {
        savedList.remove(fact)
    }
}