package ru.hse.gymvision.ui.registration

sealed class RegistrationState {
    enum class RegistrationError {
        NAME_LENGTH,
        SURNAME_LENGTH,
        LOGIN_LENGTH,
        LOGIN_CONTENT,
        PASSWORD_LENGTH,
        PASSWORD_CONTENT,
        PASSWORD_MISMATCH,
        LOGIN_TAKEN,
        REGISTRATION_FAILED,
        NETWORK,
        IDLE
    }

    data class Main(
        val name: String = "",
        val surname: String = "",
        val login: String = "",
        val password: String = "",
        val passwordVisibility: Boolean = false,
        val passwordRepeat: String = "",
        val passwordRepeatVisibility: Boolean = false,
        val nameError: RegistrationError = RegistrationError.IDLE,
        val surnameError: RegistrationError = RegistrationError.IDLE,
        val loginError: RegistrationError = RegistrationError.IDLE,
        val passwordError: RegistrationError = RegistrationError.IDLE,
        val passwordRepeatError: RegistrationError = RegistrationError.IDLE,
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
