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
        val nameIsError: Boolean = false,
        val surnameIsError: Boolean = false,
        val loginIsError: Boolean = false,
        val passwordIsError: Boolean = false,
        val passwordRepeatIsError: Boolean = false,
        val nameErrorText : String? = null,
        val surnameErrorText : String? = null,
        val loginErrorText : String? = null,
        val passwordErrorText : String? = null,
        val passwordRepeatErrorText : String? = null,
        val loading: Boolean = false
    ) : RegistrationState()

    data object Idle : RegistrationState()
    data object Loading : RegistrationState()
}

sealed class RegistrationEvent {
    data class RegistrationButtonClicked(
        val name: String,
        val surname: String,
        val login: String,
        val password: String,
        val passwordRepeat: String
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
