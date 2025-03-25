package ru.hse.gymvision.ui.authorization

sealed class AuthorizationState {
    data class Main(
        val login: String = "",
        val password: String = "",
        val passwordVisibility: Boolean = false,
        val isError: Boolean = false,
        val loading: Boolean = false
    ) : AuthorizationState()

    data object Idle : AuthorizationState()
    data object Loading : AuthorizationState()
}

sealed class AuthorizationEvent {
    data class LoginButtonClicked(
        val login: String,
        val password: String
    ): AuthorizationEvent()
    data class RegistrationButtonClicked(
        val login: String,
        val password: String
    ): AuthorizationEvent()
    data object ShowPasswordButtonClicked: AuthorizationEvent()
    data object Clear: AuthorizationEvent()
    data object CheckPastLogin: AuthorizationEvent()

}

sealed class AuthorizationAction {
    data object NavigateToGymList : AuthorizationAction()
    data object NavigateToRegistration : AuthorizationAction()
}