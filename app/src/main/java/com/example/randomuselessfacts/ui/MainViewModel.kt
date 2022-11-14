package com.example.randomuselessfacts.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randomuselessfacts.model.Fact
import com.example.randomuselessfacts.repo.Repository
import com.example.randomuselessfacts.usecase.GetFactApiUseCases
import com.example.randomuselessfacts.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val getFactApiUseCases: GetFactApiUseCases
) : ViewModel() {

    //daily fact page ui states

    var dailyFact by mutableStateOf<Resource<Fact>>(Resource.Loading())
        private set
    var randomFact by mutableStateOf<Resource<Fact>?>(null)
        private set

    var isDailyFactSaved by mutableStateOf(false)
        private set
    var isRandomFactSaved by mutableStateOf(false)
        private set

    //saved facts page ui states

    lateinit var savedFacts: StateFlow<List<Fact>>

    fun initialiseSavedFacts() {
        viewModelScope.launch(Dispatchers.IO) {
            savedFacts = repository.readFacts().stateIn(viewModelScope)
            dailyFact.data?.id?.let { isDailyFactSaved = checkIsFactSaved(it) }
            randomFact?.data?.id?.let { isRandomFactSaved = checkIsFactSaved(it) }
        }
    }

    fun getDailyFact() {
        viewModelScope.launch(Dispatchers.IO) {
            getFactApiUseCases.getDailyFact().collectLatest { resource ->
                dailyFact = resource
                dailyFact.data?.id?.let { isDailyFactSaved = checkIsFactSaved(it) }
            }
        }
    }

    fun getRandomFact() {
        if (dailyFact is Resource.Error)
            getDailyFact()
        viewModelScope.launch(Dispatchers.IO) {
            getFactApiUseCases.getRandomFact().collectLatest { resource ->
                randomFact = resource
                randomFact?.data?.id?.let { isRandomFactSaved = checkIsFactSaved(it) }
            }
        }
    }

    private suspend fun checkIsFactSaved(id: String) = repository.checkFactSaved(id)

    fun saveOrDeleteFact(fact: Fact) {
        viewModelScope.launch(Dispatchers.IO) {
            if (checkIsFactSaved(fact.id)) {
                repository.deleteFact(fact)
                if (fact.id == dailyFact.data?.id) isDailyFactSaved = false
                if (fact.id == randomFact?.data?.id) isRandomFactSaved = false
            } else {
                repository.saveFact(fact)
                if (fact.id == dailyFact.data?.id) isDailyFactSaved = true
                if (fact.id == randomFact?.data?.id) isRandomFactSaved = true
            }
        }
    }
}