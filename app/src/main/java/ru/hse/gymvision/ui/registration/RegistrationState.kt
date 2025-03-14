package ru.hse.gymvision.ui.registration

sealed class RegistrationState {
    data class Main(
        val name: String = "",
        val surname: String = "",
        val login: String = "",
        val password: String = "",
        val passwordVisibility: Boolean = false,
        val passwordRepeat: String = "",
        val passwordRepeatVisibility: Boolean = false,
        val isError: Boolean = false,
        val loading: Boolean = false
    ) : RegistrationState()

//    data object Idle : RegistrationState()
//    data object Loading : RegistrationState()
}

sealed class RegistrationEvent {
    data class RegistrationButtonClicked(
        val name: String,
        val surname: String,
        val login: String,
        val password: String
    ): RegistrationEvent()
    data class LoginButtonClicked(
        val login: String,
        val password: String
    ): RegistrationEvent()
    data object ShowPasswordButtonClicked: RegistrationEvent()
    data object ShowPasswordRepeatButtonClicked: RegistrationEvent()
    data object Clear: RegistrationEvent()
}

sealed class RegistrationAction {
    data object NavigateToGymList : RegistrationAction()
    data object NavigateToAuthorization : RegistrationAction()
}
