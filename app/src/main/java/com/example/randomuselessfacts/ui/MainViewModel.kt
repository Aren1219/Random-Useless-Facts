package com.example.randomuselessfacts.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randomuselessfacts.model.Fact
import com.example.randomuselessfacts.repo.Repository
import com.example.randomuselessfacts.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _dailyFact: MutableLiveData<Resource<Fact>> = MutableLiveData(Resource.Loading())
    val dailyFact: LiveData<Resource<Fact>> = _dailyFact

    private val _randomFact: MutableLiveData<Resource<Fact>> = MutableLiveData()
    val randomFact: LiveData<Resource<Fact>> = _randomFact

    lateinit var savedFacts: StateFlow<List<Fact>>

    init {
        viewModelScope.launch {
            savedFacts = repository.readFacts().stateIn(viewModelScope)
        }
        getDailyFact()
    }

    private fun handleResponse(response: Response<Fact>): Resource<Fact> {
        return if (response.isSuccessful) Resource.Success(response.body()!!)
        else Resource.Error(response.message())
    }

    private fun getDailyFact() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getDailyFact()
                _dailyFact.postValue(handleResponse(response))
            } catch (e: Exception) {
                _dailyFact.postValue(Resource.Error("Could not load daily fact"))
            }
        }
    }

    fun getRandomFact() {
        if (dailyFact.value is Resource.Error)
            getDailyFact()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getRandomFact()
                _randomFact.postValue(handleResponse(response))
            } catch (e: Exception) {
                _randomFact.postValue(Resource.Error("Could not load random fact"))
            }
        }
    }

    fun saveFact(fact: Fact) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveFact(fact)
    }

    fun deleteFact(fact: Fact) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteFact(fact)
    }
}