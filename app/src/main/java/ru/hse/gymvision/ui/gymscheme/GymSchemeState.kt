package ru.hse.gymvision.ui.gymscheme

import ru.hse.gymvision.domain.model.GymSchemeModel

sealed class GymSchemeState {
    data class Main(
        val gymScheme: GymSchemeModel? = null,
        val showPopup: Boolean = false,
        val showDialog: Boolean = false,
        val trainerName: String = "",
        val trainerDescription: String = ""
    ) : GymSchemeState()
    data object Loading : GymSchemeState()
}

sealed class GymSchemeEvent {
    data class LoadGymScheme(val gymId: Int) : GymSchemeEvent()
    data class TrainerClicked(
        val trainerId: Int,
        val trainerName: String,
        val trainerDescription: String
    ) : GymSchemeEvent()

    data class CameraClicked(val cameraId: Int) : GymSchemeEvent()
    data object ShowPopupEvent : GymSchemeEvent()
    data object HidePopupEvent : GymSchemeEvent()
    data object ShowDialogEvent : GymSchemeEvent()
    data object HideDialogEvent : GymSchemeEvent()
}

sealed class GymSchemeAction {
    data class ShowPopup(val trainerName: String, val trainerDescription: String) : GymSchemeAction()
    data class ShowDialog(val trainerName: String, val trainerDescription: String) : GymSchemeAction()
    data object HidePopup : GymSchemeAction()
    data object HideDialog : GymSchemeAction()
    data object NavigateToCamera : GymSchemeAction()
}