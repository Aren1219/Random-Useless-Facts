package com.example.randomuselessfacts.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.randomuselessfacts.DummyData.getDummyFact
import com.example.randomuselessfacts.getOrAwaitValue
import com.example.randomuselessfacts.model.Fact
import com.example.randomuselessfacts.repo.FakeRepository
import com.example.randomuselessfacts.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class MainViewModelTest{

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val instantTaskExecutionRule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    private lateinit var fakeRepo : FakeRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeRepository()
        viewModel = MainViewModel(fakeRepo)
        viewModel.dailyFact.getOrAwaitValue()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun cleanUp(){
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `daily fact api`() {
        assertEquals(getDummyFact(), viewModel.dailyFact.value!!.data)
    }

    @Test
    fun `random fact api`() {
        viewModel.getRandomFact()
        val result = viewModel.randomFact.getOrAwaitValue()
        assertEquals(getDummyFact(), result.data)
    }

    @Test
    fun `random fact api error`() {
        fakeRepo.errorResponse = true
        viewModel.getRandomFact()
        val result = viewModel.randomFact.getOrAwaitValue()
        assertTrue(result is Resource.Error)
    }

    @Test
    fun `save fact`() {
        viewModel.saveFact(getDummyFact())
        val result = viewModel.savedFacts.getOrAwaitValue()
        assertEquals(listOf(getDummyFact()), result)
    }

    @Test
    fun `delete fact`() {
        viewModel.saveFact(getDummyFact())
        viewModel.savedFacts.getOrAwaitValue()
        viewModel.deleteFact(getDummyFact())
        val result = viewModel.savedFacts.getOrAwaitValue()
        assertEquals(listOf<Fact>(), result)
    }
}