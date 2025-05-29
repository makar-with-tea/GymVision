package ru.hse.gymvision.ui.camera

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
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom
import ru.hse.gymvision.domain.usecase.camera.GetCameraIdsUseCase
import ru.hse.gymvision.domain.usecase.camera.GetCameraLinksUseCase
import ru.hse.gymvision.domain.usecase.camera.GetNewCameraLinkUseCase
import ru.hse.gymvision.domain.usecase.camera.MoveCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.RotateCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.SaveCamerasUseCase
import ru.hse.gymvision.domain.usecase.camera.ZoomCameraUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class CameraViewModelTest {

    private lateinit var viewModel: CameraViewModel
    private lateinit var moveCameraUseCase: MoveCameraUseCase
    private lateinit var rotateCameraUseCase: RotateCameraUseCase
    private lateinit var zoomCameraUseCase: ZoomCameraUseCase
    private lateinit var saveCamerasUseCase: SaveCamerasUseCase
    private lateinit var getCameraIdsUseCase: GetCameraIdsUseCase
    private lateinit var getCameraLinksUseCase: GetCameraLinksUseCase
    private lateinit var getNewCameraLinkUseCase: GetNewCameraLinkUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        moveCameraUseCase = mock(MoveCameraUseCase::class.java)
        rotateCameraUseCase = mock(RotateCameraUseCase::class.java)
        zoomCameraUseCase = mock(ZoomCameraUseCase::class.java)
        saveCamerasUseCase = mock(SaveCamerasUseCase::class.java)
        getCameraIdsUseCase = mock(GetCameraIdsUseCase::class.java)
        getCameraLinksUseCase = mock(GetCameraLinksUseCase::class.java)
        getNewCameraLinkUseCase = mock(GetNewCameraLinkUseCase::class.java)
        viewModel = CameraViewModel(
            moveCameraUseCase,
            rotateCameraUseCase,
            zoomCameraUseCase,
            saveCamerasUseCase,
            getCameraIdsUseCase,
            getCameraLinksUseCase,
            getNewCameraLinkUseCase,
            dispatcherIO = testDispatcher,
            dispatcherMain = testDispatcher
        )
    }

    @Test
    fun `initialize cameras without new camera`() = runTest {
        // Arrange
        val cameraIds = listOf(1, 2)
        val cameraLinks = listOf("link1", "link2")
        `when`(getCameraIdsUseCase.execute()).thenReturn(cameraIds)
        `when`(getCameraLinksUseCase.execute()).thenReturn(cameraLinks)

        // Act
        viewModel.obtainEvent(CameraEvent.InitCameras(null, 1))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(CameraState.TwoCameras(1, 2, "link1", "link2"), state)
    }

    @Test
    fun `initialize cameras with new camera`() = runTest {
        // Arrange
        val newCameraId = 3
        val gymId = 1
        val cameraIds = listOf(1, 2)
        val cameraLinks = listOf("link1", "link2")
        val newCameraLink = "link3"
        `when`(getCameraIdsUseCase.execute()).thenReturn(cameraIds)
        `when`(getCameraLinksUseCase.execute()).thenReturn(cameraLinks)
        `when`(getNewCameraLinkUseCase.execute(newCameraId, false)).thenReturn(newCameraLink)

        // Act
        viewModel.obtainEvent(CameraEvent.InitCameras(newCameraId, gymId))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(CameraState.ThreeCameras(
            3,
            1,
            2,
            "link3",
            "link1",
            "link2"
        ), state)
    }

    @Test
    fun `add camera navigates to gym scheme`() = runTest {
        // Arrange
        val cameraIds = listOf(1)
        val cameraLinks = listOf("link1")
        `when`(getCameraIdsUseCase.execute()).thenReturn(cameraIds)
        `when`(getCameraLinksUseCase.execute()).thenReturn(cameraLinks)

        viewModel.obtainEvent(CameraEvent.InitCameras(null, 1))
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(CameraEvent.AddCameraButtonClicked)
        advanceUntilIdle()

        // Assert
        val action = viewModel.action.first()
        assertEquals(CameraAction.NavigateToGymScheme, action)
    }

    @Test
    fun `move camera successfully`() = runTest {
        // Arrange
        val direction = CameraMovement.UP
        val gymId = 1
        val cameraId = 1
        `when`(getCameraIdsUseCase.execute()).thenReturn(listOf(cameraId))
        `when`(getCameraLinksUseCase.execute()).thenReturn(listOf("link1"))

        viewModel.obtainEvent(CameraEvent.InitCameras(null, gymId))
        advanceUntilIdle()
        viewModel.state.first()

        // Act
        viewModel.obtainEvent(CameraEvent.MoveCameraButtonClicked(direction))
        advanceUntilIdle()

        // Assert
        verify(moveCameraUseCase).execute(cameraId, direction)
    }

    @Test
    fun `rotate camera successfully`() = runTest {
        // Arrange
        val direction = CameraRotation.LEFT
        val gymId = 1
        val cameraId = 1
        `when`(getCameraIdsUseCase.execute()).thenReturn(listOf(cameraId))
        `when`(getCameraLinksUseCase.execute()).thenReturn(listOf("link1"))

        viewModel.obtainEvent(CameraEvent.InitCameras(null, gymId))
        advanceUntilIdle()
        viewModel.state.first()

        // Act
        viewModel.obtainEvent(CameraEvent.RotateCameraButtonClicked(direction))
        advanceUntilIdle()

        // Assert
        verify(rotateCameraUseCase).execute(cameraId, direction)
    }

    @Test
    fun `zoom camera successfully`() = runTest {
        // Arrange
        val direction = CameraZoom.IN
        val gymId = 1
        val cameraId = 1
        `when`(getCameraIdsUseCase.execute()).thenReturn(listOf(cameraId))
        `when`(getCameraLinksUseCase.execute()).thenReturn(listOf("link1"))

        viewModel.obtainEvent(CameraEvent.InitCameras(null, gymId))
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(CameraEvent.ZoomCameraButtonClicked(direction))
        advanceUntilIdle()

        // Assert
        verify(zoomCameraUseCase).execute(cameraId, direction)
    }

    @Test
    fun `delete camera successfully`() = runTest {
        // Arrange
        val cameraIds = listOf(1, 2)
        val cameraLinks = listOf("link1", "link2")
        `when`(getCameraIdsUseCase.execute()).thenReturn(cameraIds)
        `when`(getCameraLinksUseCase.execute()).thenReturn(cameraLinks)

        viewModel.obtainEvent(CameraEvent.InitCameras(null, 1))
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(CameraEvent.DeleteCameraButtonClicked(2))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(CameraState.OneCamera(1, "link1"), state)
    }

    @Test
    fun `change AI state successfully`() = runTest {
        // Arrange
        val gymId = 1
        val cameraId = 1
        val isAiEnabled = true
        `when`(getCameraIdsUseCase.execute()).thenReturn(listOf(cameraId))
        `when`(getCameraLinksUseCase.execute()).thenReturn(listOf("link1"))
        `when`(getNewCameraLinkUseCase.execute(cameraId, isAiEnabled)).thenReturn("newLink")

        viewModel.obtainEvent(CameraEvent.InitCameras(null, gymId))
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(CameraEvent.ChangeAiState(isAiEnabled))
        advanceUntilIdle()

        // Assert
        verify(getNewCameraLinkUseCase).execute(cameraId, isAiEnabled)
    }

    @Test
    fun `play camera successfully`() = runTest {
        // Arrange
        val cameraId = 1
        val cameraLink = "link1"
        `when`(getCameraIdsUseCase.execute()).thenReturn(listOf(cameraId))
        `when`(getCameraLinksUseCase.execute()).thenReturn(listOf(cameraLink))

        viewModel.obtainEvent(CameraEvent.InitCameras(null, 1))
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(CameraEvent.PlayCameraButtonClicked(1))

        // Assert
        val state = viewModel.state.first() as CameraState.OneCamera
        assertEquals(false, state.isPlaying1)
    }

    @Test
    fun `make camera main successfully`() = runTest {
        // Arrange
        val cameraIds = listOf(1, 2)
        val cameraLinks = listOf("link1", "link2")
        `when`(getCameraIdsUseCase.execute()).thenReturn(cameraIds)
        `when`(getCameraLinksUseCase.execute()).thenReturn(cameraLinks)

        viewModel.obtainEvent(CameraEvent.InitCameras(null, 1))
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(CameraEvent.MakeCameraMainButtonClicked(2))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as CameraState.TwoCameras
        assertEquals(2, state.camera1Id)
        assertEquals("link2", state.camera1Link)
    }

    @Test
    fun `clear resets state and action`() = runTest {
        // Arrange
        val cameraIds = listOf(1, 2)
        val cameraLinks = listOf("link1", "link2")
        `when`(getCameraIdsUseCase.execute()).thenReturn(cameraIds)
        `when`(getCameraLinksUseCase.execute()).thenReturn(cameraLinks)
        viewModel.obtainEvent(CameraEvent.InitCameras(null, 1))
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(CameraEvent.Clear)

        // Assert
        assertEquals(CameraState.Idle, viewModel.state.first())
        assertEquals(null, viewModel.action.first())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}