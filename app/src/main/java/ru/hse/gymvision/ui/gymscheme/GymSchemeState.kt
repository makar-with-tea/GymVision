package ru.hse.gymvision.ui.gymscheme

import ru.hse.gymvision.domain.model.GymSchemeModel

sealed class GymSchemeState {
    data object Idle : GymSchemeState()
    data class Main(
        val gymScheme: GymSchemeModel? = null,
        val showPopup: Boolean = false,
        val showDialog: Boolean = false,
        val trainerName: String = "",
        val trainerDescription: String = "",
        val selectedTrainerId: Int = -1,
        val isLoading: Boolean = false
    ) : GymSchemeState() {
        override fun toString(): String {
            return "Main(showPopup=$showPopup, showDialog=$showDialog, trainerName='$trainerName', trainerDescription='$trainerDescription', selectedTrainerId=$selectedTrainerId)"
        }
    }
    data class Error(val errorText: String) : GymSchemeState()
    data object Loading : GymSchemeState()
}

sealed class GymSchemeEvent {
    data class LoadGymScheme(val gymId: Int?) : GymSchemeEvent()
    data class TrainerClicked(
        val trainerName: String,
        val trainerDescription: String,
        val selectedTrainerId: Int
    ) : GymSchemeEvent()

    data class CameraClicked(val cameraId: Int) : GymSchemeEvent()
    data object HidePopup : GymSchemeEvent()
    data object HideDialog : GymSchemeEvent()
    data object Clear : GymSchemeEvent()
}

sealed class GymSchemeAction {
    data class NavigateToCamera(val cameraId: Int): GymSchemeAction()
}