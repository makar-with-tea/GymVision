package ru.hse.gymvision.ui.gymscheme

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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import ru.hse.gymvision.domain.model.GymSchemeModel
import ru.hse.gymvision.domain.usecase.camera.CheckCameraAccessibilityUseCase
import ru.hse.gymvision.domain.usecase.gym.GetGymIdUseCase
import ru.hse.gymvision.domain.usecase.gym.GetGymSchemeUseCase
import ru.hse.gymvision.domain.usecase.gym.SaveGymIdUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class GymSchemeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: GymSchemeViewModel
    private lateinit var getGymSchemeUseCase: GetGymSchemeUseCase
    private lateinit var checkCameraAccessibilityUseCase: CheckCameraAccessibilityUseCase
    private lateinit var getGymIdUseCase: GetGymIdUseCase
    private lateinit var saveGymIdUseCase: SaveGymIdUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getGymSchemeUseCase = mock(GetGymSchemeUseCase::class.java)
        checkCameraAccessibilityUseCase = mock(CheckCameraAccessibilityUseCase::class.java)
        getGymIdUseCase = mock(GetGymIdUseCase::class.java)
        saveGymIdUseCase = mock(SaveGymIdUseCase::class.java)
        viewModel = GymSchemeViewModel(
            getGymSchemeUseCase,
            checkCameraAccessibilityUseCase,
            getGymIdUseCase,
            saveGymIdUseCase,
            dispatcherIO = testDispatcher,
            dispatcherMain = testDispatcher
        )
    }

    @Test
    fun `load gym scheme successfully`() = runTest {
        // Arrange
        val gymId = 1
        `when`(getGymSchemeUseCase.execute(gymId)).thenReturn(gymSchemeExample)

        // Act
        viewModel.obtainEvent(GymSchemeEvent.LoadGymScheme(gymId))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(GymSchemeState.Main(gymSchemeExample), state)
    }

    @Test
    fun `load gym scheme with null initial id`() = runTest {
        // Arrange
        val gymId = 1
        `when`(getGymSchemeUseCase.execute(gymId)).thenReturn(gymSchemeExample)
        `when`(getGymIdUseCase.execute()).thenReturn(gymId)

        // Act
        viewModel.obtainEvent(GymSchemeEvent.LoadGymScheme(null))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(GymSchemeState.Main(gymSchemeExample), state)
    }

    @Test
    fun `load gym scheme with negative id`() = runTest {
        // Arrange
        val gymId = -1
        `when`(getGymSchemeUseCase.execute(gymId)).thenReturn(gymSchemeExample)
        `when`(getGymIdUseCase.execute()).thenReturn(gymId)

        // Act
        viewModel.obtainEvent(GymSchemeEvent.LoadGymScheme(null))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(GymSchemeState.Error(GymSchemeState.GymSchemeError.GYM_NOT_FOUND), state)
    }

    @Test
    fun `load gym scheme with null result`() = runTest {
        // Arrange
        val gymId = 1
        `when`(getGymSchemeUseCase.execute(gymId)).thenReturn(null)

        // Act
        viewModel.obtainEvent(GymSchemeEvent.LoadGymScheme(gymId))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(GymSchemeState.Error(GymSchemeState.GymSchemeError.GYM_NOT_FOUND), state)
    }

    @Test
    fun `load gym scheme with error`() = runTest {
        // Arrange
        val gymId = 1
        `when`(getGymSchemeUseCase.execute(gymId)).thenThrow(RuntimeException("Error"))

        // Act
        viewModel.obtainEvent(GymSchemeEvent.LoadGymScheme(gymId))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(GymSchemeState.Error(GymSchemeState.GymSchemeError.NETWORK_ERROR), state)
    }

    @Test
    fun `camera clicked and accessible`() = runTest {
        // Arrange
        val gymId = 1
        val cameraId = 2
        `when`(checkCameraAccessibilityUseCase.execute(cameraId)).thenReturn(true)
        `when`(getGymSchemeUseCase.execute(gymId)).thenReturn(gymSchemeExample)
        viewModel.obtainEvent(GymSchemeEvent.LoadGymScheme(gymId))
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(GymSchemeEvent.CameraClicked(gymId, cameraId))
        advanceUntilIdle()

        // Assert
        val action = viewModel.action.first()
        assertEquals(GymSchemeAction.NavigateToCamera(gymId, cameraId), action)
    }

    @Test
    fun `camera clicked and inaccessible`() = runTest {
        // Arrange
        val gymId = 1
        val cameraId = 2
        `when`(checkCameraAccessibilityUseCase.execute(cameraId)).thenReturn(false)
        `when`(getGymSchemeUseCase.execute(gymId)).thenReturn(gymSchemeExample)
        viewModel.obtainEvent(GymSchemeEvent.LoadGymScheme(gymId))
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(GymSchemeEvent.CameraClicked(gymId, cameraId))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as GymSchemeState.Main
        assertEquals(true, state.showDialog)
    }

    @Test
    fun `camera clicked and error occurred`() = runTest {
        // Arrange
        val gymId = 1
        val cameraId = 2
        `when`(checkCameraAccessibilityUseCase.execute(cameraId)).thenThrow(RuntimeException())
        `when`(getGymSchemeUseCase.execute(gymId)).thenReturn(gymSchemeExample)
        viewModel.obtainEvent(GymSchemeEvent.LoadGymScheme(gymId))
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(GymSchemeEvent.CameraClicked(gymId, cameraId))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as GymSchemeState.Error
        assertEquals(true, state.error == GymSchemeState.GymSchemeError.NETWORK_ERROR)
    }

    @Test
    fun `camera clicked with negative gymId`() = runTest {
        // Arrange
        val gymId = 1
        val cameraId = 2
        `when`(getGymSchemeUseCase.execute(gymId)).thenReturn(gymSchemeExample)
        viewModel.obtainEvent(GymSchemeEvent.LoadGymScheme(gymId))
        `when`(getGymIdUseCase.execute()).thenReturn(-1)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(GymSchemeEvent.CameraClicked(null, cameraId))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(GymSchemeState.Error(GymSchemeState.GymSchemeError.GYM_NOT_FOUND), state)
    }

    @Test
    fun `hide camera dialog`() = runTest {
        // Arrange
        val gymId = 1
        val cameraId = 2
        `when`(checkCameraAccessibilityUseCase.execute(cameraId)).thenReturn(false)
        `when`(getGymSchemeUseCase.execute(gymId)).thenReturn(gymSchemeExample)
        viewModel.obtainEvent(GymSchemeEvent.LoadGymScheme(gymId))
        advanceUntilIdle()
        viewModel.obtainEvent(GymSchemeEvent.CameraClicked(gymId, cameraId))
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(GymSchemeEvent.HideDialog)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as GymSchemeState.Main
        assertEquals(false, state.showDialog)
    }

    @Test
    fun `trainer clicked shows popup`() = runTest {
        // Arrange
        val trainerName = "Treadmill"
        val trainerDescription = "You can run here"
        val trainerId = 1
        val gymId = 1
        `when`(getGymSchemeUseCase.execute(gymId)).thenReturn(gymSchemeExample)
        viewModel.obtainEvent(GymSchemeEvent.LoadGymScheme(gymId))
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(GymSchemeEvent.TrainerClicked(trainerName, trainerDescription, trainerId))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as GymSchemeState.Main
        assertEquals(true, state.showPopup)
        assertEquals(trainerName, state.trainerName)
        assertEquals(trainerDescription, state.trainerDescription)
        assertEquals(trainerId, state.selectedTrainerId)
    }

    @Test
    fun `hide popup successfully`() = runTest {
        // Arrange
        val trainerName = "Treadmill"
        val trainerDescription = "You can run here"
        val trainerId = 1
        val gymId = 1
        `when`(getGymSchemeUseCase.execute(gymId)).thenReturn(gymSchemeExample)
        viewModel.obtainEvent(GymSchemeEvent.LoadGymScheme(gymId))
        advanceUntilIdle()
        viewModel.obtainEvent(GymSchemeEvent.TrainerClicked(trainerName, trainerDescription, trainerId))
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(GymSchemeEvent.HidePopup)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as GymSchemeState.Main
        assertEquals(false, state.showPopup)
        assertEquals(-1, state.selectedTrainerId)
    }

    @Test
    fun `clear resets state and action`() = runTest {
        // Act
        viewModel.obtainEvent(GymSchemeEvent.Clear)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(GymSchemeState.Idle, state)
        val action = viewModel.action.first()
        assertEquals(null, action)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    companion object {
        private val gymSchemeExample = GymSchemeModel(
            id = 1,
            name = "Gym 1",
            scheme = null,
            clickableTrainerModels = emptyList(),
            clickableCameraModels = emptyList()
        )
    }
}