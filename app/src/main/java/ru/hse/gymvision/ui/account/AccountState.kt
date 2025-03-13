package ru.hse.gymvision.ui.account

sealed class AccountState {
    data object Idle : AccountState()
    data class Main(
        val name: String = "",
        val surname: String = "",
        val login: String = "",
        val password: String = ""
    ) : AccountState()
    data class EditName(
        val name: String = "",
        val surname: String = "",
        val login: String = "",
        val password: String = ""
    ) : AccountState()

    data class ChangePassword(
        val oldPassword: String = "",
        val newPassword: String = "",
        val oldPasswordVisibility: Boolean = false,
        val newPasswordRepeatVisibility: Boolean = false,
        val name: String = "",
        val surname: String = "",
        val login: String = "",
        val password: String = "",
        val isLoading: Boolean = false
    ) : AccountState()

    data object Loading : AccountState()
}

sealed class AccountEvent {
    data class GetUserInfo(val id: Int) : AccountEvent()
    data class SaveNameButtonClicked(
        val name: String,
        val surname: String
    ): AccountEvent()
    data class SavePasswordButtonClicked(
        val newPassword: String
    ): AccountEvent()
    data class ShowOldPasswordButtonClicked(
        val oldPasswordVisibility: Boolean
    ): AccountEvent()
    data class ShowNewPasswordButtonClicked(
        val newPasswordRepeatVisibility: Boolean
    ): AccountEvent()
    data object EditNameButtonClicked : AccountEvent()
    data object EditPasswordButtonClicked: AccountEvent()
    data object DeleteAccountButtonClicked: AccountEvent()
    data object LogoutButtonClicked: AccountEvent()
    data object Clear: AccountEvent()
}

sealed class AccountAction {
    data object NavigateToAuthorization : AccountAction()
}