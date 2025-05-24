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
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import ru.hse.gymvision.domain.model.UserModel
import ru.hse.gymvision.domain.usecase.user.ChangePasswordUseCase
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

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getUserInfoUseCase = mock(GetUserInfoUseCase::class.java)
        changePasswordUseCase = mock(ChangePasswordUseCase::class.java)
        updateUserUseCase = mock(UpdateUserUseCase::class.java)
        deleteUserUseCase = mock(DeleteUserUseCase::class.java)
        logoutUseCase = mock(LogoutUseCase::class.java)
        viewModel = AccountViewModel(
            getUserInfoUseCase,
            changePasswordUseCase,
            updateUserUseCase,
            deleteUserUseCase,
            logoutUseCase,
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
        assertEquals(userExample.password, state.password)
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
        val password = userExample.password
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)
        doNothing().`when`(updateUserUseCase.execute(name, surname, login))

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditNameButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SaveNameButtonClicked(name, surname))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(AccountState.Main(name, surname, login, password), state)
    }

    @Test
    fun `save name with invalid name`() = runTest {
        // Arrange
        val name = "A"
        val surname = userExample.surname
        val login = userExample.login
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)
        doNothing().`when`(updateUserUseCase.execute(name, surname, login))

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditNameButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SaveNameButtonClicked(name, surname))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as AccountState.EditName
        assertEquals(AccountState.AccountError.NAME_LENGTH, state.nameError)
    }

    @Test
    fun `save name with invalid surname`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = "A"
        val login = userExample.login
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)
        doNothing().`when`(updateUserUseCase.execute(name, surname, login))

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
        val state = viewModel.state.first() as AccountState.EditName
        assertEquals(AccountState.AccountError.NETWORK, state.nameError)
        assertEquals(AccountState.AccountError.NETWORK, state.surnameError)
    }

    @Test
    fun `save password successfully`() = runTest {
        // Arrange
        val newPassword = "newPassword1"
        val oldPassword = userExample.password
        val realPassword = userExample.password

        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)
        doNothing().`when`(changePasswordUseCase.execute(userExample.login, newPassword))

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SavePasswordButtonClicked(newPassword, oldPassword, realPassword))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as AccountState.Main
        assertEquals(newPassword, state.password)
    }

    @Test
    fun `save password with incorrect old password`() = runTest {
        // Arrange
        val newPassword = "newPassword1"
        val oldPassword = "wrongPassword"
        val realPassword = userExample.password
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)
        doNothing().`when`(changePasswordUseCase.execute(userExample.login, newPassword))

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SavePasswordButtonClicked(newPassword, oldPassword, realPassword))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as AccountState.ChangePassword
        assertEquals(AccountState.AccountError.PASSWORD_INCORRECT, state.oldPasswordError)
    }

    @Test
    fun `save password with weak new password`() = runTest {
        // Arrange
        val newPassword = "newPassword"
        val oldPassword = userExample.password
        val realPassword = userExample.password
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)
        doNothing().`when`(changePasswordUseCase.execute(userExample.login, newPassword))

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SavePasswordButtonClicked(newPassword, oldPassword, realPassword))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as AccountState.ChangePassword
        assertEquals(AccountState.AccountError.PASSWORD_CONTENT, state.newPasswordError)
    }

    @Test
    fun `save password with short new password`() = runTest {
        // Arrange
        val newPassword = "A"
        val oldPassword = userExample.password
        val realPassword = userExample.password
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)
        doNothing().`when`(changePasswordUseCase.execute(userExample.login, newPassword))

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SavePasswordButtonClicked(newPassword, oldPassword, realPassword))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as AccountState.ChangePassword
        assertEquals(AccountState.AccountError.PASSWORD_LENGTH, state.newPasswordError)
    }

    @Test
    fun `save password with error`() = runTest {
        // Arrange
        val newPassword = "newPassword1"
        val oldPassword = userExample.password
        val realPassword = userExample.password

        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)
        `when`(changePasswordUseCase.execute(userExample.login, newPassword)).thenThrow(RuntimeException("Error"))

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()
        viewModel.obtainEvent(AccountEvent.EditPasswordButtonClicked)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.SavePasswordButtonClicked(newPassword, oldPassword, realPassword))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as AccountState.ChangePassword
        assertEquals(AccountState.AccountError.NETWORK, state.oldPasswordError)
        assertEquals(AccountState.AccountError.NETWORK, state.newPasswordError)
        assertEquals(AccountState.AccountError.NETWORK, state.newPasswordRepeatError)
    }

    @Test
    fun `delete account successfully`() = runTest {
        // Arrange
        val login = userExample.login
        doNothing().`when`(deleteUserUseCase.execute(login))
        `when`(getUserInfoUseCase.execute()).thenReturn(userExample)

        viewModel.obtainEvent(AccountEvent.GetUserInfo)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AccountEvent.DeleteAccountButtonClicked(login))
        advanceUntilIdle()

        // Assert
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
        val state = viewModel.state.first() as AccountState.DeletionError
        assertEquals(login, state.login)
    }

    @Test
    fun `logout successfully`() = runTest {
        // Act
        viewModel.obtainEvent(AccountEvent.LogoutButtonClicked)
        advanceUntilIdle()

        // Assert
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


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    companion object {
        val userExample = UserModel(
            name = "John",
            surname = "Doe",
            login = "johndoe",
            password = "password1"
        )
    }
}