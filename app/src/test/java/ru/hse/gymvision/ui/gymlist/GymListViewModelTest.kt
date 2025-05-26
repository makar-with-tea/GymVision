package ru.hse.gymvision.ui.gymlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import ru.hse.gymvision.domain.model.GymInfoModel
import ru.hse.gymvision.domain.usecase.gym.GetGymListUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class GymListViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: GymListViewModel
    private lateinit var getGymListUseCase: GetGymListUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getGymListUseCase = mock(GetGymListUseCase::class.java)
        viewModel = GymListViewModel(
            getGymListUseCase,
            dispatcherIO = testDispatcher,
            dispatcherMain = testDispatcher
        )
    }

    @Test
    fun `fetch gym list successfully`() = runTest {
        // Arrange
        val gyms = listOf(
            GymInfoModel(1, "Gym 1", "Address 1"),
            GymInfoModel(2, "Gym 1", "Address 1")
        )
        `when`(getGymListUseCase.execute()).thenReturn(gyms)

        // Act
        viewModel.obtainEvent(GymListEvent.GetGymList)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(GymListState.Main(gyms), state)
    }

    @Test
    fun `fetch gym list with error`() = runTest {
        // Arrange
        `when`(getGymListUseCase.execute()).thenThrow(RuntimeException("Error"))

        // Act
        viewModel.obtainEvent(GymListEvent.GetGymList)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(GymListState.Error, state)
    }

    @Test
    fun `select gym navigates to gym`() = runTest {
        // Arrange
        val gymId = 1

        // Act
        viewModel.obtainEvent(GymListEvent.SelectGym(gymId))
        advanceUntilIdle()

        // Assert
        val action = viewModel.action.first()
        assertEquals(GymListAction.NavigateToGym(gymId), action)
    }

    @Test
    fun `clear resets state and action`() = runTest {
        // Act
        viewModel.obtainEvent(GymListEvent.Clear)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(GymListState.Idle, state)
        val action = viewModel.action.first()
        assertEquals(null, action)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}