package ru.hse.gymvision.ui.account

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import ru.hse.gymvision.domain.model.UserModel
import ru.hse.gymvision.domain.usecase.user.ChangePasswordUseCase
import ru.hse.gymvision.domain.usecase.user.CheckPasswordUseCase
import ru.hse.gymvision.domain.usecase.user.DeleteUserUseCase
import ru.hse.gymvision.domain.usecase.user.GetUserInfoUseCase
import ru.hse.gymvision.domain.usecase.user.LogoutUseCase
import ru.hse.gymvision.domain.usecase.user.UpdateUserUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class AccountViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AccountViewModel
    private lateinit var getUserInfoUseCase: GetUserInfoUseCase
    private lateinit var changePasswordUseCase: ChangePasswordUseCase
    private lateinit var updateUserUseCase: UpdateUserUseCase
    private lateinit var deleteUserUseCase: DeleteUserUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var checkPasswordUseCase: CheckPasswordUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getUserInfoUseCase = mock(GetUserInfoUseCase::class.java)
        changePasswordUseCase = mock(ChangePasswordUseCase::class.java)
        updateUserUseCase = mock(UpdateUserUseCase::class.java)
        deleteUserUseCase = mock(DeleteUserUseCase::class.java)
        logoutUseCase = mock(LogoutUseCase::class.java)
        checkPasswordUseCase = mock(CheckPasswordUseCase::class.java)
        viewModel = AccountViewModel(
            getUserInfoUseCase,
            changePasswordUseCase,
            updateUserUseCase,
            deleteUserUseCase,
            logoutUseCase,
            checkPasswordUseCase,
            dispatcherIO = testDispatcher,
            dispatcherMain = testDispatcher
        )
    }

    @Test
    fun `fetch user info successfully`() = runTest {
        // Arrange
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        // Act
        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as AccountState.Main
        verify(getUserInfoUseCase).execute()
        assertEquals(userExample.login, state.login)
        assertEquals(userExample.name, state.name)
        assertEquals(userExample.surname, state.surname)
    }

    @Test
    fun `fetch user info with error`() = runTest {
        // Arrange
        `when`(getUserInfoUseCase.execute()).thenThrow(RuntimeException("Error"))

        // Act
        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(AccountState.Error(AccountState.AccountError.NETWORK_FATAL), state)
    }

    @Test
    fun `fetch null user info`() = runTest {
        // Arrange
        `when`(getUserInfoUseCase.execute()).thenReturn(null)

        // Act
        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(AccountState.Error(AccountState.AccountError.ACCOUNT_NOT_FOUND), state)
    }

    @Test
    fun `save name successfully`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = userExample.surname
        val login = userExample.login
        val email = userExample.email
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditNameButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SaveNameButtonClicked(name, surname))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(AccountState.Main(name, surname, email, login), state)
    }

    @Test
    fun `save name with invalid name`() = runTest {
        // Arrange
        val name = "A"
        val surname = userExample.surname
        val login = userExample.login
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditNameButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SaveNameButtonClicked(name, surname))
        advanceUntilIdle()

        // Assert
        verify(updateUserUseCase, never()).execute(name, surname, login)
        val state = viewModel.state.first() as AccountState.EditName
        assertEquals(AccountState.AccountError.NAME_LENGTH, state.nameError)
    }

    @Test
    fun `save name with invalid surname`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = "A"
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditNameButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SaveNameButtonClicked(name, surname))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as AccountState.EditName
        assertEquals(AccountState.AccountError.SURNAME_LENGTH, state.surnameError)
    }

    @Test
    fun `save name with error`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = userExample.surname
        val login = userExample.login
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)
        `when`(updateUserUseCase.execute(name, surname, login)).thenThrow(RuntimeException("Error"))

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditNameButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SaveNameButtonClicked(name, surname))
        advanceUntilIdle()

        // Assert
        verify(updateUserUseCase).execute(name, surname, login)
        val state = viewModel.state.first() as AccountState.EditName
        assertEquals(AccountState.AccountError.NETWORK, state.nameError)
        assertEquals(AccountState.AccountError.NETWORK, state.surnameError)
    }

    @Test
    fun `save name with invalid characters in name`() = runTest {
        // Arrange
        val name = "John1"
        val surname = userExample.surname
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditNameButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SaveNameButtonClicked(name, surname))
        advanceUntilIdle()

        // Assert
        verify(updateUserUseCase, never()).execute(name, surname, userExample.login)
        val state = viewModel.state.first() as AccountState.EditName
        assertEquals(AccountState.AccountError.NAME_CONTENT, state.nameError)
    }

    @Test
    fun `save name with invalid characters in surname`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = "Doe@"
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditNameButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SaveNameButtonClicked(name, surname))
        advanceUntilIdle()

        // Assert
        verify(updateUserUseCase, never()).execute(name, surname, userExample.login)
        val state = viewModel.state.first() as AccountState.EditName
        assertEquals(AccountState.AccountError.SURNAME_CONTENT, state.surnameError)
    }

    @Test
    fun `save password successfully`() = runTest {
        // Arrange
        val newPassword = "newPassword1"
        val oldPassword = PASSWORD_EXAMPLE
        val newPasswordRepeat = "newPassword1"

        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)
        `when`(checkPasswordUseCase.execute(userExample.login, oldPassword))
            .thenReturn(true)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SavePasswordButtonClicked(
            newPassword, oldPassword, newPasswordRepeat
        ))
        advanceUntilIdle()

        // Assert
        verify(changePasswordUseCase).execute(userExample.login, newPassword)
    }

    @Test
    fun `save password with incorrect old password`() = runTest {
        // Arrange
        val newPassword = "newPassword1"
        val oldPassword = "wrongPassword"
        val newPasswordRepeat = "newPassword1"
        `when`(checkPasswordUseCase.execute(userExample.login, oldPassword))
            .thenReturn(false)
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SavePasswordButtonClicked(
            newPassword, oldPassword, newPasswordRepeat
        ))
        advanceUntilIdle()

        // Assert
        verify(checkPasswordUseCase).execute(userExample.login, oldPassword)
        val state = viewModel.state.first() as AccountState.ChangePassword
        assertEquals(AccountState.AccountError.PASSWORD_INCORRECT, state.oldPasswordError)
    }

    @Test
    fun `save password with weak new password`() = runTest {
        // Arrange
        val newPassword = "newPassword"
        val oldPassword = PASSWORD_EXAMPLE
        val newPasswordRepeat = "newPassword"
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)
        `when`(checkPasswordUseCase.execute(userExample.login, oldPassword))
            .thenReturn(true)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SavePasswordButtonClicked(
            newPassword, oldPassword, newPasswordRepeat
        ))
        advanceUntilIdle()

        // Assert
        verify(checkPasswordUseCase, never()).execute(userExample.login, newPassword)
        val state = viewModel.state.first() as AccountState.ChangePassword
        assertEquals(AccountState.AccountError.PASSWORD_CONTENT, state.newPasswordError)
    }

    @Test
    fun `save password with short new password`() = runTest {
        // Arrange
        val newPassword = "A"
        val oldPassword = PASSWORD_EXAMPLE
        val newPasswordRepeat = "A"
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SavePasswordButtonClicked(
            newPassword, oldPassword, newPasswordRepeat
        ))
        advanceUntilIdle()

        // Assert
        verify(checkPasswordUseCase, never()).execute(userExample.login, newPassword)
        val state = viewModel.state.first() as AccountState.ChangePassword
        assertEquals(AccountState.AccountError.PASSWORD_LENGTH, state.newPasswordError)
    }

    @Test
    fun `save password with error`() = runTest {
        // Arrange
        val newPassword = "newPassword1"
        val oldPassword = PASSWORD_EXAMPLE
        val newPasswordRepeat = "newPassword1"

        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)
        `when`(checkPasswordUseCase.execute(userExample.login, oldPassword))
            .thenReturn(true)
        `when`(changePasswordUseCase.execute(userExample.login, newPassword))
            .thenThrow(RuntimeException("Error"))

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SavePasswordButtonClicked(
            newPassword, oldPassword, newPasswordRepeat
        ))
        advanceUntilIdle()

        // Assert
        verify(changePasswordUseCase).execute(userExample.login, newPassword)
        val state = viewModel.state.first() as AccountState.ChangePassword
        assertEquals(AccountState.AccountError.NETWORK, state.oldPasswordError)
        assertEquals(AccountState.AccountError.NETWORK, state.newPasswordError)
        assertEquals(AccountState.AccountError.NETWORK, state.newPasswordRepeatError)
    }

    @Test
    fun `delete account successfully`() = runTest {
        // Arrange
        val login = userExample.login
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.DeleteAccountButtonClicked(login))
        advanceUntilIdle()

        // Assert
        verify(deleteUserUseCase).execute(login)
        val action = viewModel.action.first()
        assertEquals(AccountAction.NavigateToAuthorization, action)
    }

    @Test
    fun `delete account with error`() = runTest {
        // Arrange
        val login = userExample.login
        `when`(deleteUserUseCase.execute(login)).thenThrow(RuntimeException("Error"))
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.DeleteAccountButtonClicked(login))
        advanceUntilIdle()

        // Assert
        verify(deleteUserUseCase).execute(login)
        val state = viewModel.state.first() as AccountState.DeletionError
        assertEquals(login, state.login)
    }

    @Test
    fun `logout successfully`() = runTest {
        // Act
        viewModel.obtainEvent(AccountEvent.LogoutButtonClicked)
        advanceUntilIdle()

        // Assert
        verify(logoutUseCase).execute()
        val action = viewModel.action.first()
        assertEquals(AccountAction.NavigateToAuthorization, action)
    }

    @Test
    fun `clear viewmodel`() = runTest {
        // Act
        viewModel.obtainEvent(
            AccountEvent.Clear
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(AccountState.Idle, state)
        val action = viewModel.action.first()
        assertEquals(null, action)
    }

    @Test
    fun `change old password visibility`() = runTest {
        // Arrange
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
        advanceUntilIdle()
        val initialState = viewModel.state.first() as AccountState.ChangePassword

        // Act
        viewModel.obtainEvent(AccountEvent.ShowOldPasswordButtonClicked)
        advanceUntilIdle()

        // Assert
        val newState = viewModel.state.first() as AccountState.ChangePassword
        assertEquals(!initialState.oldPasswordVisibility, newState.oldPasswordVisibility)
    }

    @Test
    fun `change new password visibility`() = runTest {
        // Arrange
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
        advanceUntilIdle()
        val initialState = viewModel.state.first() as AccountState.ChangePassword

        // Act
        viewModel.obtainEvent(AccountEvent.ShowNewPasswordButtonClicked)
        advanceUntilIdle()

        // Assert
        val newState = viewModel.state.first() as AccountState.ChangePassword
        assertEquals(!initialState.newPasswordVisibility, newState.newPasswordVisibility)
    }

    @Test
    fun `change new password repeat visibility`() = runTest {
        // Arrange
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
        advanceUntilIdle()
        val initialState = viewModel.state.first() as AccountState.ChangePassword

        // Act
        viewModel.obtainEvent(AccountEvent.ShowNewPasswordRepeatButtonClicked)
        advanceUntilIdle()

        // Assert
        val newState = viewModel.state.first() as AccountState.ChangePassword
        assertEquals(!initialState.newPasswordRepeatVisibility, newState.newPasswordRepeatVisibility)
    }

    @Test
    fun `save password with mismatched new password repeat`() = runTest {
        // Arrange
        val newPassword = "newPassword1"
        val oldPassword = PASSWORD_EXAMPLE
        val newPasswordRepeat = "differentPassword"
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SavePasswordButtonClicked(
            newPassword, oldPassword, newPasswordRepeat
        ))
        advanceUntilIdle()

        // Assert
        verify(changePasswordUseCase, never()).execute(userExample.login, newPassword)
        val state = viewModel.state.first() as AccountState.ChangePassword
        assertEquals(AccountState.AccountError.PASSWORD_MISMATCH, state.newPasswordRepeatError)
    }

    @Test
    fun `save email successfully`() = runTest {
        // Arrange
        val email = "newemail@example.com"
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditEmailButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SaveEmailButtonClicked(email))
        advanceUntilIdle()

        // Assert
        verify(updateUserUseCase).execute(
            name = userExample.name,
            surname = userExample.surname,
            email = email,
            login = userExample.login
        )
        val state = viewModel.state.first()
        assertEquals(AccountState.Main(
            userExample.name, userExample.surname, email, userExample.login), state)
    }

    @Test
    fun `save email with invalid email`() = runTest {
        // Arrange
        val email = "invalid-email"
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditEmailButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SaveEmailButtonClicked(email))
        advanceUntilIdle()

        // Assert
        verify(updateUserUseCase, never()).execute(
            name = userExample.name,
            surname = userExample.surname,
            email = email,
            login = userExample.login
        )
        val state = viewModel.state.first() as AccountState.ChangeEmail
        assertEquals(AccountState.AccountError.EMAIL_CONTENT, state.emailError)
    }

    @Test
    fun `save email with network error`() = runTest {
        // Arrange
        val email = "newemail@example.com"
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)
        `when`(updateUserUseCase.execute(
            name = userExample.name,
            surname = userExample.surname,
            email = email,
            login = userExample.login
        )).thenThrow(RuntimeException())

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditEmailButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SaveEmailButtonClicked(email))
        advanceUntilIdle()

        // Assert
        verify(updateUserUseCase).execute(
            name = userExample.name,
            surname = userExample.surname,
            email = email,
            login = userExample.login
        )
        val state = viewModel.state.first() as AccountState.ChangeEmail
        assertEquals(AccountState.AccountError.NETWORK, state.emailError)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    companion object {
        val userExample = UserModel(
            name = "John",
            surname = "Doe",
            login = "johndoe",
            email = "johndoe@example.com"
        )
        const val PASSWORD_EXAMPLE = "password1"
    }
}
