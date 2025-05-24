package ru.hse.gymvision.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.libvlc.media.MediaPlayer
import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom
import ru.hse.gymvision.domain.usecase.camera.ChangeAiStateUseCase
import ru.hse.gymvision.domain.usecase.camera.GetCameraIdsUseCase
import ru.hse.gymvision.domain.usecase.camera.GetCameraLinksUseCase
import ru.hse.gymvision.domain.usecase.camera.GetNewCameraLinkUseCase
import ru.hse.gymvision.domain.usecase.camera.MoveCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.RotateCameraUseCase
import ru.hse.gymvision.domain.usecase.camera.SaveCamerasUseCase
import ru.hse.gymvision.domain.usecase.camera.ZoomCameraUseCase

class CameraViewModel(
    private val moveCameraUseCase: MoveCameraUseCase,
    private val rotateCameraUseCase: RotateCameraUseCase,
    private val zoomCameraUseCase: ZoomCameraUseCase,
    private val saveCamerasUseCase: SaveCamerasUseCase,
    private val getCameraIdsUseCase: GetCameraIdsUseCase,
    private val getCameraLinksUseCase: GetCameraLinksUseCase,
    private val getNewCameraLinkUseCase: GetNewCameraLinkUseCase,
    private val changeAIState: ChangeAiStateUseCase,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
): ViewModel() {
    private var mediaPlayer1: MediaPlayer? = null
    private var mediaPlayer2: MediaPlayer? = null
    private var mediaPlayer3: MediaPlayer? = null
    private var gymId: Int = -1

    private val _state: MutableStateFlow<CameraState> =
        MutableStateFlow(CameraState.Idle)
    val state: StateFlow<CameraState>
        get() = _state
    private val _action = MutableStateFlow<CameraAction?>(null)
    val action: StateFlow<CameraAction?>
        get() = _action

    override fun onCleared() {
        super.onCleared()
        // Release media players to free resources
        mediaPlayer1?.release()
        mediaPlayer2?.release()
        mediaPlayer3?.release()
    }

    fun obtainEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.AddCameraButtonClicked -> addCamera()
            is CameraEvent.Clear -> clear()
            is CameraEvent.MoveCameraButtonClicked -> moveCamera(event.direction)
            is CameraEvent.PlayCameraButtonClicked -> playCamera(event.cameraNum)
            is CameraEvent.RotateCameraButtonClicked -> rotateCamera(event.direction)
            is CameraEvent.ZoomCameraButtonClicked -> zoomCamera(event.direction)
            is CameraEvent.DeleteCameraButtonClicked -> deleteCamera(event.cameraNum)
            is CameraEvent.InitCameras -> initCameras(event.newCameraId, event.gymId)
            is CameraEvent.MakeCameraMainButtonClicked -> makeCameraMain(event.cameraNum)
            is CameraEvent.SavePlayers -> savePlayers(event.player1, event.player2, event.player3)
            is CameraEvent.ChangeAiState -> changeAIState(event.isAiEnabled)
        }
    }

    private fun clear() {
        _state.value = CameraState.Idle
        _action.value = null
    }

    private fun savePlayers(player1: MediaPlayer?, player2: MediaPlayer?, player3: MediaPlayer?) {
        // Release previous media players if they somehow weren't released earlier
        mediaPlayer1?.release()
        mediaPlayer2?.release()
        mediaPlayer3?.release()

        mediaPlayer1 = player1
        mediaPlayer2 = player2
        mediaPlayer3 = player3
    }

    private fun initCameras(newCameraId: Int?, gymId: Int) {
        if (_state.value != CameraState.Idle) {
            return
        }
        this.gymId = gymId
        _state.value = CameraState.Loading
        viewModelScope.launch(dispatcherIO) {
            val cameras = getCameraIdsUseCase.execute().toMutableList()
            val cameraLinks = getCameraLinksUseCase.execute().toMutableList()

            if (newCameraId != null && !cameras.contains(newCameraId)) {
                val newCameraLink = getNewCameraLinkUseCase.execute(gymId, newCameraId)
                cameras.add(0, newCameraId)
                cameraLinks.add(0, newCameraLink)
                saveCamerasUseCase.execute(cameras, cameraLinks)
            }
            withContext(dispatcherMain) {
                _state.value = when (cameras.size) {
                    1 -> CameraState.OneCamera(
                        camera1Id = cameras[0],
                        camera1Link = cameraLinks[0],
                    )

                    2 -> CameraState.TwoCameras(
                        camera1Id = cameras[0],
                        camera1Link = cameraLinks[0],
                        camera2Id = cameras[1],
                        camera2Link = cameraLinks[1],
                    )

                    3 -> {
                        CameraState.ThreeCameras(
                            camera1Id = cameras[0],
                            camera1Link = cameraLinks[0],
                            camera2Id = cameras[1],
                            camera2Link = cameraLinks[1],
                            camera3Id = cameras[2],
                            camera3Link = cameraLinks[2],
                        )
                    }

                    else -> CameraState.Idle
                }
            }
        }
    }

    private fun addCamera() {
        if (_state.value !is CameraState.OneCamera && _state.value !is CameraState.TwoCameras) {
            return
        }
        _action.value = CameraAction.NavigateToGymScheme
    }

    private fun moveCamera(direction: CameraMovement) {
        val cameraId = when (_state.value) {
            is CameraState.OneCamera -> (_state.value as CameraState.OneCamera).camera1Id
            is CameraState.TwoCameras -> (_state.value as CameraState.TwoCameras).camera1Id
            is CameraState.ThreeCameras -> (_state.value as CameraState.ThreeCameras).camera1Id
            else -> return
        }
        viewModelScope.launch(dispatcherIO) {
            moveCameraUseCase.execute(gymId, cameraId, direction)
        }
    }

    private fun rotateCamera(direction: CameraRotation) {
        val cameraId = when (_state.value) {
            is CameraState.OneCamera -> (_state.value as CameraState.OneCamera).camera1Id
            is CameraState.TwoCameras -> (_state.value as CameraState.TwoCameras).camera1Id
            is CameraState.ThreeCameras -> (_state.value as CameraState.ThreeCameras).camera1Id
            else -> return
        }
        viewModelScope.launch(dispatcherIO) {
            rotateCameraUseCase.execute(gymId, cameraId, direction)
        }
    }

    private fun zoomCamera(direction: CameraZoom) {
        val cameraId = when (_state.value) {
            is CameraState.OneCamera -> (_state.value as CameraState.OneCamera).camera1Id
            is CameraState.TwoCameras -> (_state.value as CameraState.TwoCameras).camera1Id
            is CameraState.ThreeCameras -> (_state.value as CameraState.ThreeCameras).camera1Id
            else -> return
        }
        viewModelScope.launch(dispatcherIO) {
            zoomCameraUseCase.execute(gymId, cameraId, direction)
        }
    }

    private fun deleteCamera(cameraNum: Int) {
        when (cameraNum) {
            2 -> {
                if (_state.value !is CameraState.TwoCameras &&
                    _state.value !is CameraState.ThreeCameras
                ) {
                    return
                }
                val newCameraIds: List<Int> = when (_state.value) {
                    is CameraState.TwoCameras -> listOf(
                        (_state.value as CameraState.TwoCameras).camera1Id
                    )

                    is CameraState.ThreeCameras -> listOf(
                        (_state.value as CameraState.ThreeCameras).camera1Id,
                        (_state.value as CameraState.ThreeCameras).camera3Id
                    )

                    else -> return
                }
                val newCameraLinks: List<String> = when (_state.value) {
                    is CameraState.TwoCameras -> listOf(
                        (_state.value as
                                CameraState.TwoCameras).camera1Link
                    )

                    is CameraState.ThreeCameras -> listOf(
                        (_state.value as CameraState.ThreeCameras).camera1Link,
                        (_state.value as CameraState.ThreeCameras).camera3Link
                    )

                    else -> return
                }
                viewModelScope.launch(dispatcherIO) {
                    saveCamerasUseCase.execute(newCameraIds, newCameraLinks)
                    withContext(dispatcherMain) {
                        _state.value = when (_state.value) {
                            is CameraState.ThreeCameras -> {
                                CameraState.TwoCameras(
                                    camera1Id =
                                        (_state.value as CameraState.ThreeCameras).camera1Id,
                                    camera1Link =
                                        (_state.value as CameraState.ThreeCameras).camera1Link,
                                    isPlaying1 =
                                        (_state.value as CameraState.ThreeCameras).isPlaying1,
                                    camera2Id =
                                        (_state.value as CameraState.ThreeCameras).camera3Id,
                                    camera2Link =
                                        (_state.value as CameraState.ThreeCameras).camera3Link,
                                    isPlaying2 =
                                        (_state.value as CameraState.ThreeCameras).isPlaying3
                                )
                            }

                            is CameraState.TwoCameras -> {
                                CameraState.OneCamera(
                                    camera1Id =
                                        (_state.value as CameraState.TwoCameras).camera1Id,
                                    camera1Link =
                                        (_state.value as CameraState.TwoCameras).camera1Link,
                                    isPlaying1 =
                                        (_state.value as CameraState.TwoCameras).isPlaying1
                                )
                            }

                            else -> CameraState.Idle
                        }
                    }
                }

                mediaPlayer2?.release()
                mediaPlayer2 = mediaPlayer3
                mediaPlayer3 = null
            }

            3 -> {
                if (_state.value !is CameraState.ThreeCameras) {
                    return
                }
                val newCameraIds: List<Int> = listOf(
                    (_state.value as CameraState.ThreeCameras).camera1Id,
                    (_state.value as CameraState.ThreeCameras).camera2Id
                )
                val newCameraLinks: List<String> = listOf(
                    (_state.value as CameraState.ThreeCameras).camera1Link,
                    (_state.value as CameraState.ThreeCameras).camera2Link
                )
                viewModelScope.launch(dispatcherIO) {
                    saveCamerasUseCase.execute(newCameraIds, newCameraLinks)
                    withContext(dispatcherMain) {
                        _state.value = CameraState.TwoCameras(
                            camera1Id = (_state.value as CameraState.ThreeCameras).camera1Id,
                            camera1Link = (_state.value as CameraState.ThreeCameras).camera1Link,
                            isPlaying1 = (_state.value as CameraState.ThreeCameras).isPlaying1,
                            camera2Id = (_state.value as CameraState.ThreeCameras).camera2Id,
                            camera2Link = (_state.value as CameraState.ThreeCameras).camera2Link,
                            isPlaying2 = (_state.value as CameraState.ThreeCameras).isPlaying2
                        )
                    }
                }

                mediaPlayer3?.release()
                mediaPlayer3 = null
            }
        }
    }

    private fun playCamera(cameraNum: Int) {
        when (cameraNum) {
            1 -> _state.value = when (_state.value) {
                is CameraState.OneCamera -> (_state.value as CameraState.OneCamera).copy(
                    isPlaying1 = !(_state.value as CameraState.OneCamera).isPlaying1
                )

                is CameraState.TwoCameras -> (_state.value as CameraState.TwoCameras).copy(
                    isPlaying1 = !(_state.value as CameraState.TwoCameras).isPlaying1
                )

                is CameraState.ThreeCameras -> (_state.value as CameraState.ThreeCameras).copy(
                    isPlaying1 = !(_state.value as CameraState.ThreeCameras).isPlaying1
                )

                else -> CameraState.Idle
            }

            2 -> _state.value = when (_state.value) {
                is CameraState.TwoCameras -> (_state.value as CameraState.TwoCameras).copy(
                    isPlaying2 = !(_state.value as CameraState.TwoCameras).isPlaying2
                )

                is CameraState.ThreeCameras -> (_state.value as CameraState.ThreeCameras).copy(
                    isPlaying2 = !(_state.value as CameraState.ThreeCameras).isPlaying2
                )

                else -> CameraState.Idle
            }

            3 -> _state.value = when (_state.value) {
                is CameraState.ThreeCameras -> (_state.value as CameraState.ThreeCameras).copy(
                    isPlaying3 = !(_state.value as CameraState.ThreeCameras).isPlaying3
                )

                else -> CameraState.Idle
            }

            else -> CameraState.Idle
        }
    }

    private fun makeCameraMain(cameraNum: Int) {
        when (cameraNum) {
            2 -> {
                if (_state.value !is CameraState.ThreeCameras && _state.value !is CameraState.TwoCameras) {
                    return
                }
                val newCameraIds: List<Int> = when (_state.value) {
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
                val newCameraLinks: List<String> = when (_state.value) {
                    is CameraState.TwoCameras -> listOf(
                        (_state.value as CameraState.TwoCameras).camera2Link,
                        (_state.value as CameraState.TwoCameras).camera1Link
                    )

                    is CameraState.ThreeCameras -> listOf(
                        (_state.value as CameraState.ThreeCameras).camera2Link,
                        (_state.value as CameraState.ThreeCameras).camera1Link,
                        (_state.value as CameraState.ThreeCameras).camera3Link
                    )

                    else -> return
                }
                viewModelScope.launch(dispatcherIO) {
                    saveCamerasUseCase.execute(newCameraIds, newCameraLinks)
                    withContext(dispatcherMain) {
                        _state.value = when (_state.value) {
                            is CameraState.ThreeCameras -> {
                                CameraState.ThreeCameras(
                                    camera1Id = newCameraIds[0],
                                    camera1Link = (_state.value as CameraState.ThreeCameras).camera2Link,
                                    isPlaying1 = (_state.value as CameraState.ThreeCameras).isPlaying2,
                                    camera2Id = newCameraIds[1],
                                    camera2Link = (_state.value as CameraState.ThreeCameras).camera1Link,
                                    isPlaying2 = (_state.value as CameraState.ThreeCameras).isPlaying1,
                                    camera3Id = newCameraIds[2],
                                    camera3Link = (_state.value as CameraState.ThreeCameras).camera3Link,
                                    isPlaying3 = (_state.value as CameraState.ThreeCameras).isPlaying3
                                )
                            }

                            is CameraState.TwoCameras -> {
                                CameraState.TwoCameras(
                                    camera1Id = newCameraIds[0],
                                    camera1Link = (_state.value as CameraState.TwoCameras).camera2Link,
                                    isPlaying1 = (_state.value as CameraState.TwoCameras).isPlaying2,
                                    camera2Id = newCameraIds[1],
                                    camera2Link = (_state.value as CameraState.TwoCameras).camera1Link,
                                    isPlaying2 = (_state.value as CameraState.TwoCameras).isPlaying1
                                )
                            }

                            else -> CameraState.Idle
                        }
                    }
                }

                val temp = mediaPlayer1
                mediaPlayer1 = mediaPlayer2
                mediaPlayer2 = temp
            }

            3 -> {
                if (_state.value !is CameraState.ThreeCameras) {
                    return
                }
                val newCameraIds: List<Int> = listOf(
                    (_state.value as CameraState.ThreeCameras).camera3Id,
                    (_state.value as CameraState.ThreeCameras).camera1Id,
                    (_state.value as CameraState.ThreeCameras).camera2Id
                )
                val newCameraLinks: List<String> = listOf(
                    (_state.value as CameraState.ThreeCameras).camera3Link,
                    (_state.value as CameraState.ThreeCameras).camera1Link,
                    (_state.value as CameraState.ThreeCameras).camera2Link
                )
                viewModelScope.launch(dispatcherIO) {
                    saveCamerasUseCase.execute(newCameraIds, newCameraLinks)
                    withContext(dispatcherMain) {
                        _state.value = CameraState.ThreeCameras(
                            camera1Id = newCameraIds[0],
                            camera1Link = (_state.value as CameraState.ThreeCameras).camera3Link,
                            isPlaying1 = (_state.value as CameraState.ThreeCameras).isPlaying3,
                            camera2Id = newCameraIds[1],
                            camera2Link = (_state.value as CameraState.ThreeCameras).camera1Link,
                            isPlaying2 = (_state.value as CameraState.ThreeCameras).isPlaying1,
                            camera3Id = newCameraIds[2],
                            camera3Link = (_state.value as CameraState.ThreeCameras).camera2Link,
                            isPlaying3 = (_state.value as CameraState.ThreeCameras).isPlaying2
                        )
                    }
                }

                val temp = mediaPlayer1
                mediaPlayer1 = mediaPlayer3
                mediaPlayer3 = mediaPlayer2
                mediaPlayer2 = temp
            }
        }
    }

    private fun changeAIState(isAiEnabled: Boolean) {
        val cameraId = when (_state.value) {
            is CameraState.OneCamera -> (_state.value as CameraState.OneCamera).camera1Id
            is CameraState.TwoCameras -> (_state.value as CameraState.TwoCameras).camera1Id
            is CameraState.ThreeCameras -> (_state.value as CameraState.ThreeCameras).camera1Id
            else -> return
        }
        viewModelScope.launch(dispatcherIO) {
            changeAIState.execute(gymId, cameraId, isAiEnabled)
        }
    }
}
