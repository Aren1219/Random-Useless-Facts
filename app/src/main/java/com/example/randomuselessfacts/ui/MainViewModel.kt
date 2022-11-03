package com.example.randomuselessfacts.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randomuselessfacts.model.Fact
import com.example.randomuselessfacts.repo.Repository
import com.example.randomuselessfacts.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    //daily fact page ui states

    private val _dailyFact: MutableStateFlow<Resource<Fact>> = MutableStateFlow(Resource.Loading())
    val dailyFact: StateFlow<Resource<Fact>> = _dailyFact

    private val _randomFact: MutableStateFlow<Resource<Fact>?> = MutableStateFlow(null)
    val randomFact: StateFlow<Resource<Fact>?> = _randomFact

    val isDailyFactSaved = mutableStateOf(false)
    val isRandomFactSaved = mutableStateOf(false)

    //saved facts page ui states

    var savedFacts: StateFlow<List<Fact>> = MutableStateFlow(listOf())

    init {
        initialiseSavedFacts()
        getDailyFact()
    }

    private fun initialiseSavedFacts() = viewModelScope.launch(Dispatchers.IO) {
        savedFacts = repository.readFacts().stateIn(viewModelScope)
        isDailyFactSaved.value = dailyFact.value.data?.id?.let { checkIsFactSaved(it) } == true
        isRandomFactSaved.value = randomFact.value?.data?.id?.let { checkIsFactSaved(it) } == true
    }

    private fun handleResponse(response: Response<Fact>): Resource<Fact> {
        return if (response.isSuccessful) Resource.Success(response.body()!!)
        else Resource.Error(response.message())
    }

    private fun getDailyFact() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _dailyFact.emit(Resource.Loading())
                val response = repository.getDailyFact()
                _dailyFact.emit(handleResponse(response))
                response.body()?.id?.let {
                    isDailyFactSaved.value = checkIsFactSaved(it)
                }
            } catch (e: HttpException) {
                _dailyFact.emit(Resource.Error("Could not load daily fact"))
            } catch (e: IOException) {
                _dailyFact.emit(Resource.Error("Check internet"))
            }
        }
    }

    fun getRandomFact() {
        if (dailyFact.value is Resource.Error)
            getDailyFact()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _randomFact.emit(Resource.Loading())
                val response = repository.getRandomFact()
                _randomFact.emit(handleResponse(response))
                response.body()?.id?.let {
                    isRandomFactSaved.value = checkIsFactSaved(it)
                }
            } catch (e: HttpException) {
                _randomFact.emit(Resource.Error("Could not load random fact"))
            } catch (e: IOException) {
                _randomFact.emit(Resource.Error("Check internet"))
            }
        }
    }

    private suspend fun checkIsFactSaved(id: String) = repository.checkFactSaved(id)

    fun saveOrDeleteFact(fact: Fact) {
        viewModelScope.launch(Dispatchers.IO) {
            if (checkIsFactSaved(fact.id)) {
                repository.deleteFact(fact)
                if (fact.id == _dailyFact.value.data?.id) isDailyFactSaved.value = false
                if (fact.id == _randomFact.value?.data?.id) isRandomFactSaved.value = false
            } else {
                repository.saveFact(fact)
                if (fact.id == _dailyFact.value.data?.id) isDailyFactSaved.value = true
                if (fact.id == _randomFact.value?.data?.id) isRandomFactSaved.value = true
            }
        }
    }
}