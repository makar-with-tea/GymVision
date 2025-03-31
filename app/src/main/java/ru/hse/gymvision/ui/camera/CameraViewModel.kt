package ru.hse.gymvision.ui.camera

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom
import ru.hse.gymvision.domain.usecase.camera.GetCameraIdsUseCase
import ru.hse.gymvision.domain.usecase.camera.MoveCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.RotateCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.SaveCameraIdsUseCase
import ru.hse.gymvision.domain.usecase.camera.ZoomCameraUseCase

class CameraViewModel(
    private val moveCameraUseCase: MoveCameraUseCase,
    private val rotateCameraUseCase: RotateCameraUseCase,
    private val zoomCameraUseCase: ZoomCameraUseCase,
    private val saveCameraIdsUseCase: SaveCameraIdsUseCase,
    private val getCameraIdsUseCase: GetCameraIdsUseCase
    ): ViewModel() {
    private val _state: MutableStateFlow<CameraState> =
        MutableStateFlow(CameraState.Idle)
    val state: StateFlow<CameraState>
        get() = _state
    private val _action = MutableStateFlow<CameraAction?>(null)
    val action: StateFlow<CameraAction?>
        get() = _action

    fun obtainEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.AddCameraButtonClicked -> addCamera()
            is CameraEvent.Clear -> clear()
            is CameraEvent.MoveCameraButtonClicked -> moveCamera(event.direction)
            is CameraEvent.PlayFirstCameraButtonClicked -> playFirstCamera()
            is CameraEvent.PlaySecondCameraButtonClicked -> playSecondCamera()
            is CameraEvent.PlayThirdCameraButtonClicked -> playThirdCamera()
            is CameraEvent.RotateCameraButtonClicked -> rotateCamera(event.direction)
            is CameraEvent.ZoomCameraButtonClicked -> zoomCamera(event.direction)
            is CameraEvent.DeleteSecondCameraButtonClicked -> deleteSecondCamera()
            is CameraEvent.DeleteThirdCameraButtonClicked -> deleteThirdCamera()
            is CameraEvent.LoadCameraIds -> loadCameraIds(event.newCameraId)
            CameraEvent.MakeSecondCameraMainButtonClicked -> makeSecondCameraMain()
            CameraEvent.MakeThirdCameraMainButtonClicked -> makeThirdCameraMain()
        }
    }

    private fun clear() {
        _state.value = CameraState.Idle
        _action.value = null
    }

    private fun loadCameraIds(newCameraId: Int?) {
        if (_state.value != CameraState.Idle) {
            return
        }
        _state.value = CameraState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val cameras = getCameraIdsUseCase.execute()?.toMutableList() ?: mutableListOf()
            Log.d("CameraViewModel", "cameras: $cameras, newCameraId: $newCameraId")
            if (newCameraId != null && !cameras.contains(newCameraId)) {
                cameras.add(0, newCameraId)
                saveCameraIdsUseCase.execute(cameras)
            }
            withContext(Dispatchers.Main) {
                _state.value = when (cameras.size) {
                    1 -> CameraState.OneCamera(
                        camera1Id = cameras[0]
                    )

                    2 -> CameraState.TwoCameras(
                        camera1Id = cameras[0],
                        camera2Id = cameras[1]
                    )

                    3 -> CameraState.ThreeCameras(
                        camera1Id = cameras[0],
                        camera2Id = cameras[1],
                        camera3Id = cameras[2]
                    )

                    else -> CameraState.Idle
                }
            }
        }
    }

    private fun addCamera() {
        _action.value = CameraAction.NavigateToGymScheme
    }

    private fun moveCamera(direction: CameraMovement) {
        val cameraId = when (_state.value) {
            is CameraState.OneCamera -> (_state.value as CameraState.OneCamera).camera1Id
            is CameraState.TwoCameras -> (_state.value as CameraState.TwoCameras).camera1Id
            is CameraState.ThreeCameras -> (_state.value as CameraState.ThreeCameras).camera1Id
            else -> return
        }
        viewModelScope.launch(Dispatchers.IO) {
            moveCameraUseCase.execute(cameraId, direction)
        }
    }

    private fun rotateCamera(direction: CameraRotation) {
        val cameraId = when (_state.value) {
            is CameraState.OneCamera -> (_state.value as CameraState.OneCamera).camera1Id
            is CameraState.TwoCameras -> (_state.value as CameraState.TwoCameras).camera1Id
            is CameraState.ThreeCameras -> (_state.value as CameraState.ThreeCameras).camera1Id
            else -> return
        }
        viewModelScope.launch(Dispatchers.IO) {
            rotateCameraUseCase.execute(cameraId, direction)
        }
    }

    private fun zoomCamera(direction: CameraZoom) {
        val cameraId = when (_state.value) {
            is CameraState.OneCamera -> (_state.value as CameraState.OneCamera).camera1Id
            is CameraState.TwoCameras -> (_state.value as CameraState.TwoCameras).camera1Id
            is CameraState.ThreeCameras -> (_state.value as CameraState.ThreeCameras).camera1Id
            else -> return
        }
        viewModelScope.launch(Dispatchers.IO) {
            zoomCameraUseCase.execute(cameraId, direction)
        }
    }

    private fun deleteSecondCamera() {
        if (_state.value !is CameraState.TwoCameras && _state.value !is CameraState.ThreeCameras) {
            return
        }
        val newCameras: List<Int> = when (_state.value) {
            is CameraState.TwoCameras -> listOf((_state.value as CameraState.TwoCameras).camera1Id)
            is CameraState.ThreeCameras -> listOf(
                (_state.value as CameraState.ThreeCameras).camera1Id,
                (_state.value as CameraState.ThreeCameras).camera3Id
            )
            else -> return
        }
        viewModelScope.launch(Dispatchers.IO) {
            saveCameraIdsUseCase.execute(newCameras)
            withContext(Dispatchers.Main) {
                _state.value = when (_state.value) {
                    is CameraState.ThreeCameras -> {
                        CameraState.TwoCameras(
                            camera1Id = (_state.value as CameraState.ThreeCameras).camera1Id,
                            isPlaying1 = (_state.value as CameraState.ThreeCameras).isPlaying1,
                            camera2Id = (_state.value as CameraState.ThreeCameras).camera3Id,
                            isPlaying2 = (_state.value as CameraState.ThreeCameras).isPlaying3
                        )
                    }
                    is CameraState.TwoCameras -> {
                        CameraState.OneCamera(
                            camera1Id = (_state.value as CameraState.TwoCameras).camera1Id,
                            isPlaying1 = (_state.value as CameraState.TwoCameras).isPlaying1
                        )
                    }
                    else -> CameraState.Idle
                }
            }
        }
    }

    private fun deleteThirdCamera() {
        if (_state.value !is CameraState.ThreeCameras) {
            return
        }
        val newCameras: List<Int> = listOf(
            (_state.value as CameraState.ThreeCameras).camera1Id,
            (_state.value as CameraState.ThreeCameras).camera2Id
        )
        viewModelScope.launch(Dispatchers.IO) {
            saveCameraIdsUseCase.execute(newCameras)
            withContext(Dispatchers.Main) {
                _state.value = CameraState.TwoCameras(
                    camera1Id = (_state.value as CameraState.ThreeCameras).camera1Id,
                    isPlaying1 = (_state.value as CameraState.ThreeCameras).isPlaying1,
                    camera2Id = (_state.value as CameraState.ThreeCameras).camera2Id,
                    isPlaying2 = (_state.value as CameraState.ThreeCameras).isPlaying2
                )
            }
        }
    }

    private fun playFirstCamera() {
        _state.value = when (_state.value) {
            is CameraState.OneCamera -> (_state.value as CameraState.OneCamera).copy(isPlaying1 = !(_state.value as CameraState.OneCamera).isPlaying1)
            is CameraState.TwoCameras -> (_state.value as CameraState.TwoCameras).copy(isPlaying1 = !(_state.value as CameraState.TwoCameras).isPlaying1)
            is CameraState.ThreeCameras -> (_state.value as CameraState.ThreeCameras).copy(isPlaying1 = !(_state.value as CameraState.ThreeCameras).isPlaying1)
            else -> CameraState.Idle
        }
    }

    private fun playSecondCamera() {
        _state.value = when (_state.value) {
            is CameraState.TwoCameras -> (_state.value as CameraState.TwoCameras).copy(isPlaying2 = !(_state.value as CameraState.TwoCameras).isPlaying2)
            is CameraState.ThreeCameras -> (_state.value as CameraState.ThreeCameras).copy(isPlaying2 = !(_state.value as CameraState.ThreeCameras).isPlaying2)
            else -> CameraState.Idle
        }
    }

    private fun playThirdCamera() {
        _state.value = when (_state.value) {
            is CameraState.ThreeCameras -> (_state.value as CameraState.ThreeCameras).copy(isPlaying3 = !(_state.value as CameraState.ThreeCameras).isPlaying3)
            else -> CameraState.Idle
        }
    }

    private fun makeSecondCameraMain() {
        if (_state.value !is CameraState.ThreeCameras && _state.value !is CameraState.TwoCameras) {
            return
        }
        val newCameras: List<Int> = when (_state.value) {
            is CameraState.TwoCameras -> listOf(
                (_state.value as CameraState.TwoCameras).camera2Id,
                (_state.value as CameraState.TwoCameras).camera1Id
            )
            is CameraState.ThreeCameras -> listOf(
                (_state.value as CameraState.ThreeCameras).camera2Id,
                (_state.value as CameraState.ThreeCameras).camera1Id,
                (_state.value as CameraState.ThreeCameras).camera3Id
            )
            else -> return
        }
        viewModelScope.launch(Dispatchers.IO) {
            saveCameraIdsUseCase.execute(newCameras)
            withContext(Dispatchers.Main) {
                _state.value = when (_state.value) {
                    is CameraState.ThreeCameras -> {
                        CameraState.ThreeCameras(
                            camera1Id = newCameras[0],
                            isPlaying1 = (_state.value as CameraState.ThreeCameras).isPlaying2,
                            camera2Id = newCameras[1],
                            isPlaying2 = (_state.value as CameraState.ThreeCameras).isPlaying1,
                            camera3Id = newCameras[2],
                            isPlaying3 = (_state.value as CameraState.ThreeCameras).isPlaying3
                        )
                    }
                    is CameraState.TwoCameras -> {
                        CameraState.TwoCameras(
                            camera1Id = newCameras[0],
                            isPlaying1 = (_state.value as CameraState.TwoCameras).isPlaying2,
                            camera2Id = newCameras[1],
                            isPlaying2 = (_state.value as CameraState.TwoCameras).isPlaying1
                        )
                    }
                    else -> CameraState.Idle
                }
            }
        }
    }

    private fun makeThirdCameraMain() {
        if (_state.value !is CameraState.ThreeCameras) {
            return
        }
        val newCameras: List<Int> = listOf(
            (_state.value as CameraState.ThreeCameras).camera3Id,
            (_state.value as CameraState.ThreeCameras).camera1Id,
            (_state.value as CameraState.ThreeCameras).camera2Id
        )
        viewModelScope.launch(Dispatchers.IO) {
            saveCameraIdsUseCase.execute(newCameras)
            withContext(Dispatchers.Main) {
                _state.value = CameraState.ThreeCameras(
                    camera1Id = newCameras[0],
                    isPlaying1 = (_state.value as CameraState.ThreeCameras).isPlaying3,
                    camera2Id = newCameras[1],
                    isPlaying2 = (_state.value as CameraState.ThreeCameras).isPlaying1,
                    camera3Id = newCameras[2],
                    isPlaying3 = (_state.value as CameraState.ThreeCameras).isPlaying2
                )
            }
        }
    }
}