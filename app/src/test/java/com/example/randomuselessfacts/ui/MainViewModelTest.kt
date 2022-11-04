package com.example.randomuselessfacts.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cash.turbine.test
import com.example.randomuselessfacts.DummyData.getDummyFact
import com.example.randomuselessfacts.model.Fact
import com.example.randomuselessfacts.repo.FakeRepository
import com.example.randomuselessfacts.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class MainViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val instantTaskExecutionRule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel
    private lateinit var fakeRepo: FakeRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeRepository()
        viewModel = MainViewModel(fakeRepo)
    }

    @Test
    fun `daily fact and random fact api calls`() = runBlocking {
        viewModel.getRandomFact()
        viewModel.dailyFact.test {
            assertTrue(this.awaitItem() is Resource.Loading)
            assertEquals(getDummyFact(), this.awaitItem().data)
        }
        viewModel.randomFact.test {
            assertEquals(getDummyFact(), this.awaitItem()?.data)
        }
    }

    @Test
    fun `random fact api error`() = runBlocking {
        fakeRepo.errorResponse = true
        viewModel.getRandomFact()
        viewModel.randomFact.test {
            assertTrue(this.awaitItem() is Resource.Loading)
            assertTrue(this.awaitItem() is Resource.Error)
        }
    }

    @Test
    fun `save fact`() = runBlocking {
        viewModel.saveOrDeleteFact(getDummyFact())
        fakeRepo.readFacts().test {
            assertEquals(listOf(getDummyFact()), this.awaitItem())
        }
    }

    @Test
    fun `save multiple facts`() = runBlocking {
        viewModel.saveOrDeleteFact(getDummyFact("1"))
        fakeRepo.readFacts().test {
            assertEquals(listOf(getDummyFact("1")), this.awaitItem())
        }
        viewModel.saveOrDeleteFact(getDummyFact("2"))
        fakeRepo.readFacts().test {
            assertEquals(listOf(getDummyFact("1"), getDummyFact("2")), this.awaitItem())
        }
    }

    @Test
    fun `delete facts`() = runBlocking {
        viewModel.saveOrDeleteFact(getDummyFact("1"))
        fakeRepo.readFacts().test {
            assertEquals(listOf(getDummyFact("1")), this.awaitItem())
        }
        viewModel.saveOrDeleteFact(getDummyFact("1"))
        fakeRepo.readFacts().test {
            assertEquals(listOf<List<Fact>>(), this.awaitItem())
        }
    }
}