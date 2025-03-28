package ru.hse.gymvision.ui.camera

import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom

sealed class CameraState {
    data class OneCamera(
        val camera1Id: Int = 0,
        val isPlaying1: Boolean = true,
    ): CameraState()
    data class TwoCameras(
        val camera1Id: Int = 0,
        val camera2Id: Int = 0,
        val isPlaying1: Boolean = true,
        val isPlaying2: Boolean = true,
    ): CameraState()
    data class ThreeCameras(
        val camera1Id: Int = 0,
        val camera2Id: Int = 0,
        val camera3Id: Int = 0,
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
    data object PlayFirstCameraButtonClicked : CameraEvent()
    data object PlaySecondCameraButtonClicked : CameraEvent()
    data object PlayThirdCameraButtonClicked : CameraEvent()
    data object DeleteSecondCameraButtonClicked : CameraEvent()
    data object DeleteThirdCameraButtonClicked : CameraEvent()
    data class LoadCameraIds(val newCameraId: Int?) : CameraEvent()
    data object Clear: CameraEvent()
}

sealed class CameraAction {
    data object NavigateToGymScheme : CameraAction()
}