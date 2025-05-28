package ru.hse.gymvision.ui.gymscheme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.gymvision.domain.usecase.camera.CheckCameraAccessibilityUseCase
import ru.hse.gymvision.domain.usecase.gym.GetGymIdUseCase
import ru.hse.gymvision.domain.usecase.gym.GetGymSchemeUseCase
import ru.hse.gymvision.domain.usecase.gym.SaveGymIdUseCase
import kotlin.properties.Delegates

class GymSchemeViewModel(
    private val getGymSchemeUseCase: GetGymSchemeUseCase,
    private val checkCameraAccessibilityUseCase: CheckCameraAccessibilityUseCase,
    private val getGymIdUseCase: GetGymIdUseCase,
    private val saveGymIdUseCase: SaveGymIdUseCase,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    private val _state: MutableStateFlow<GymSchemeState> = MutableStateFlow(GymSchemeState.Idle)
    val state: StateFlow<GymSchemeState>
        get() = _state
    private val _action = MutableStateFlow<GymSchemeAction?>(null)
    val action: StateFlow<GymSchemeAction?>
        get() = _action

    fun obtainEvent(event: GymSchemeEvent) {
        when (event) {
            is GymSchemeEvent.LoadGymScheme -> {
                loadGymScheme(event.gymId)
            }
            is GymSchemeEvent.TrainerClicked -> {
                onTrainerClicked(event.trainerName, event.trainerDescription, event.selectedTrainerId)
            }
            is GymSchemeEvent.CameraClicked -> {
                onCameraClicked(
                    event.gymId,
                    event.cameraId
                )
            }
            is GymSchemeEvent.HidePopup -> {
                hidePopup()
            }
            is GymSchemeEvent.HideDialog -> {
                hideDialog()
            }
            is GymSchemeEvent.Clear -> clear()
        }
    }

    private fun loadGymScheme(gymId: Int?) {
        _state.value = GymSchemeState.Loading
        viewModelScope.launch(dispatcherIO) {
            val id = if (gymId == null) {
                getGymIdUseCase.execute()
            } else {
                saveGymIdUseCase.execute(gymId)
                gymId
            }
            if (id < 0) {
                withContext(dispatcherMain) {
                    _state.value = GymSchemeState.Error(GymSchemeState.GymSchemeError.GYM_NOT_FOUND)
                }
                return@launch
            }
            try {
                val gymScheme = getGymSchemeUseCase.execute(id) ?: run {
                    withContext(dispatcherMain) {
                        _state.value =
                            GymSchemeState.Error(GymSchemeState.GymSchemeError.GYM_NOT_FOUND)
                    }
                    return@launch
                }
                withContext(dispatcherMain) {
                    _state.value = GymSchemeState.Main(gymScheme = gymScheme)
                }
            } catch (e: Exception) {
                withContext(dispatcherMain) {
                    _state.value = GymSchemeState.Error(GymSchemeState.GymSchemeError.NETWORK_ERROR)
                }
            }
        }
    }

    private fun onCameraClicked(gymId: Int?, cameraId: Int) {
        if (state.value !is GymSchemeState.Main) return
        _state.value = (state.value as GymSchemeState.Main).copy(isLoading = true)
        viewModelScope.launch(dispatcherIO) {
            val id = if (gymId == null) {
                getGymIdUseCase.execute()
            } else {
                saveGymIdUseCase.execute(gymId)
                gymId
            }
            if (id < 0) {
                withContext(dispatcherMain) {
                    _state.value = GymSchemeState.Error(GymSchemeState.GymSchemeError.GYM_NOT_FOUND)
                }
                return@launch
            }
            var isAccessible by Delegates.notNull<Boolean>()
            try {
                isAccessible = checkCameraAccessibilityUseCase.execute(cameraId)
            } catch (e: Exception) {
                withContext(dispatcherMain) {
                    _state.value = GymSchemeState.Error(GymSchemeState.GymSchemeError.NETWORK_ERROR)
                }
                return@launch
            }
            withContext(dispatcherMain) {
                if (!isAccessible) {
                    if (state.value is GymSchemeState.Main) {
                        _state.value = (state.value as GymSchemeState.Main).copy(showDialog = true)
                    }
                } else {
                    _action.value = GymSchemeAction.NavigateToCamera(id, cameraId)
                }
            }
        }
    }
    private fun onTrainerClicked(trainerName: String, trainerDescription: String, selectedTrainerId: Int) {
        if (state.value !is GymSchemeState.Main) return
        _state.value = (state.value as GymSchemeState.Main).copy(
            showPopup = true,
            trainerName = trainerName,
            trainerDescription = trainerDescription,
            selectedTrainerId = selectedTrainerId
        )
    }

    private fun hidePopup() {
        if (state.value is GymSchemeState.Main) {
            _state.value = (state.value as GymSchemeState.Main).copy(showPopup = false, selectedTrainerId = -1)
        }
    }
    private fun hideDialog() {
        if (state.value is GymSchemeState.Main) {
            _state.value = (state.value as GymSchemeState.Main).copy(showDialog = false)
        }
    }
    private fun clear() {
        _state.value = GymSchemeState.Idle
        _action.value = null
    }
}