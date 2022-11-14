package com.example.randomuselessfacts.usecase

import com.example.randomuselessfacts.model.Fact
import com.example.randomuselessfacts.repo.Repository
import com.example.randomuselessfacts.util.Resource
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class GetFactApiUseCases @Inject constructor(
    private val repository: Repository
) {

    private fun handleResponse(response: Response<Fact>): Resource<Fact> {
        return if (response.isSuccessful) Resource.Success(response.body()!!)
        else Resource.Error(response.message())
    }

    fun getDailyFact() = flow {
        emit(Resource.Loading())
        try {
            val response = repository.getDailyFact()
            emit(handleResponse(response))
        } catch (e: HttpException) {
            emit(Resource.Error("Could not load daily fact"))
        } catch (e: IOException) {
            emit(Resource.Error("Check internet"))
        }
    }

    fun getRandomFact() = flow {
        emit(Resource.Loading())
        try {
            val response = repository.getRandomFact()
            emit(handleResponse(response))
        } catch (e: HttpException) {
            emit(Resource.Error("Could not load daily fact"))
        } catch (e: IOException) {
            emit(Resource.Error("Check internet"))
        }
    }
}