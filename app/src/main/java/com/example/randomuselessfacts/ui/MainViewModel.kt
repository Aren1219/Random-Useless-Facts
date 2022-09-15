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
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: Repository
): ViewModel() {

    private val _dailyFact: MutableLiveData<Resource<Fact>> = MutableLiveData(Resource.Loading())
    val dailyFact: LiveData<Resource<Fact>> = _dailyFact

    private val _randomFact: MutableLiveData<Resource<Fact>> = MutableLiveData()
    val randomFact: LiveData<Resource<Fact>> = _randomFact

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getDailyFact()
            _dailyFact.postValue(handleResponse(response))
        }
    }

    private fun handleResponse(response: Response<Fact>): Resource<Fact> {
        return if (response.isSuccessful) Resource.Success(response.body()!!)
        else Resource.Error(response.message())
    }

    fun getRandomFact() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getRandomFact()
            _randomFact.postValue(handleResponse(response))
        }
    }
}