package ru.hse.gymvision.ui.camera

import org.videolan.libvlc.MediaPlayer
import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom

sealed class CameraState {
    data class OneCamera(
        val camera1Id: Int = 0,
        val camera1Link: String = "",
        val isPlaying1: Boolean = true,
    ): CameraState()
    data class TwoCameras(
        val camera1Id: Int = 0,
        val camera2Id: Int = 0,
        val camera1Link: String = "",
        val camera2Link: String = "",
        val isPlaying1: Boolean = true,
        val isPlaying2: Boolean = true,
    ): CameraState()
    data class ThreeCameras(
        val camera1Id: Int = 0,
        val camera2Id: Int = 0,
        val camera3Id: Int = 0,
        val camera1Link: String = "",
        val camera2Link: String = "",
        val camera3Link: String = "",
        val isPlaying1: Boolean = true,
        val isPlaying2: Boolean = true,
        val isPlaying3: Boolean = true,
    ): CameraState()
    data object Idle : CameraState()
    data object Loading : CameraState()
}

sealed class CameraEvent {
    data object AddCameraButtonClicked : CameraEvent()
    data class RotateCameraButtonClicked(val direction: CameraRotation) : CameraEvent()
    data class MoveCameraButtonClicked(val direction: CameraMovement) : CameraEvent()
    data class ZoomCameraButtonClicked(val direction: CameraZoom) : CameraEvent()
    data class PlayCameraButtonClicked(val cameraNum: Int) : CameraEvent()
    data class DeleteCameraButtonClicked(val cameraNum: Int) : CameraEvent()
    data class MakeCameraMainButtonClicked(val cameraNum: Int) : CameraEvent()
    data class ChangeAiState(val isAiEnabled: Boolean) : CameraEvent()
    data class InitCameras(val newCameraId: Int?, val gymId: Int) : CameraEvent()
    data object Clear : CameraEvent()
    data class SavePlayers(
        val player1: MediaPlayer,
        val player2: MediaPlayer? = null,
        val player3: MediaPlayer? = null
    ) : CameraEvent()
}

sealed class CameraAction {
    data object NavigateToGymScheme : CameraAction()
}