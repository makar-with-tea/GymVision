package ru.hse.gymvision.ui.registration

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
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import ru.hse.gymvision.domain.model.UserModel
import ru.hse.gymvision.domain.usecase.user.CheckLoginAvailableUseCase
import ru.hse.gymvision.domain.usecase.user.RegisterUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RegistrationViewModel
    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var checkLoginAvailableUseCase: CheckLoginAvailableUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        registerUseCase = mock(RegisterUseCase::class.java)
        checkLoginAvailableUseCase = mock(CheckLoginAvailableUseCase::class.java)
        viewModel = RegistrationViewModel(registerUseCase, checkLoginAvailableUseCase)
    }

    @Test
    fun `successful registration`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = userExample.surname
        val login = userExample.login
        val password = userExample.password
        val passwordRepeat = userExample.password
        `when`(checkLoginAvailableUseCase.execute(login)).thenReturn(true)
        `when`(registerUseCase.execute(name, surname, login, password)).thenReturn(true)

        // Act
        viewModel.obtainEvent(
            RegistrationEvent.RegistrationButtonClicked(name, surname, login, password, passwordRepeat)
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first { it is RegistrationState.Main && !it.isLoading }
                as RegistrationState.Main
        assertEquals(name, state.name)
        assertEquals(surname, state.surname)
        assertEquals(login, state.login)
        assertEquals(password, state.password)
        assertEquals(passwordRepeat, state.passwordRepeat)
        val action = viewModel.action.first { it != null }
        assertEquals(RegistrationAction.NavigateToGymList, action)
    }

    @Test
    fun `registration with short login`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = userExample.surname
        val login = "jd"
        val password = userExample.password
        val passwordRepeat = userExample.password

        // Act
        viewModel.obtainEvent(
            RegistrationEvent.RegistrationButtonClicked(
                name, surname, login, password, passwordRepeat
            )
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first { it is RegistrationState.Main && !it.isLoading }
                as RegistrationState.Main
        assertEquals(RegistrationState.RegistrationError.LOGIN_LENGTH, state.loginError)
    }

    @Test
    fun `registration with mismatched passwords`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = userExample.surname
        val login = userExample.login
        val password = userExample.password
        val passwordRepeat = "password2"

        // Act
        viewModel.obtainEvent(
            RegistrationEvent.RegistrationButtonClicked(name, surname, login, password, passwordRepeat)
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first { it is RegistrationState.Main && !it.isLoading } as RegistrationState.Main
        assertEquals(RegistrationState.RegistrationError.PASSWORD_MISMATCH, state.passwordRepeatError)
    }

    @Test
    fun `registration with taken login`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = userExample.surname
        val login = userExample.login
        val password = userExample.password
        val passwordRepeat = userExample.password
        `when`(checkLoginAvailableUseCase.execute(login)).thenReturn(false)

        // Act
        viewModel.obtainEvent(
            RegistrationEvent.RegistrationButtonClicked(name, surname, login, password, passwordRepeat)
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first { it is RegistrationState.Main && !it.isLoading } as RegistrationState.Main
        assertEquals(RegistrationState.RegistrationError.LOGIN_TAKEN, state.loginError)
    }

    @Test
    fun `registration with incorrect name`() = runTest {
        // Arrange
        val name = "A"
        val surname = userExample.surname
        val login = userExample.login
        val password = userExample.password
        val passwordRepeat = userExample.password

        // Act
        viewModel.obtainEvent(
            RegistrationEvent.RegistrationButtonClicked(name, surname, login, password, passwordRepeat)
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first { it is RegistrationState.Main && !it.isLoading } as RegistrationState.Main
        assertEquals(RegistrationState.RegistrationError.NAME_LENGTH, state.nameError)
    }

    @Test
    fun `registration with incorrect surname`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = "B"
        val login = userExample.login
        val password = userExample.password
        val passwordRepeat = userExample.password

        // Act
        viewModel.obtainEvent(
            RegistrationEvent.RegistrationButtonClicked(name, surname, login, password, passwordRepeat)
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first { it is RegistrationState.Main && !it.isLoading } as RegistrationState.Main
        assertEquals(RegistrationState.RegistrationError.SURNAME_LENGTH, state.surnameError)
    }

    @Test
    fun `registration with weak password`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = userExample.surname
        val login = userExample.login
        val password = "pass"
        val passwordRepeat = "pass"

        // Act
        viewModel.obtainEvent(
            RegistrationEvent.RegistrationButtonClicked(name, surname, login, password, passwordRepeat)
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first { it is RegistrationState.Main && !it.isLoading } as RegistrationState.Main
        assertEquals(RegistrationState.RegistrationError.PASSWORD_CONTENT, state.passwordError)
    }

    @Test
    fun `registration with short password`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = userExample.surname
        val login = userExample.login
        val password = "p1"
        val passwordRepeat = "p1"

        // Act
        viewModel.obtainEvent(
            RegistrationEvent.RegistrationButtonClicked(name, surname, login, password, passwordRepeat)
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first { it is RegistrationState.Main && !it.isLoading } as RegistrationState.Main
        assertEquals(RegistrationState.RegistrationError.PASSWORD_LENGTH, state.passwordError)
    }

    @Test
    fun `registration with forbidden symbols in login`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = userExample.surname
        val login = "johndoe!"
        val password = userExample.password
        val passwordRepeat = userExample.password

        // Act
        viewModel.obtainEvent(
            RegistrationEvent.RegistrationButtonClicked(name, surname, login, password, passwordRepeat)
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first { it is RegistrationState.Main && !it.isLoading } as RegistrationState.Main
        assertEquals(RegistrationState.RegistrationError.LOGIN_CONTENT, state.loginError)
    }

    @Test
    fun `registration failed`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = userExample.surname
        val login = userExample.login
        val password = userExample.password
        val passwordRepeat = userExample.password
        `when`(checkLoginAvailableUseCase.execute(login)).thenReturn(true)
        `when`(registerUseCase.execute(name, surname, login, password)).thenReturn(false)

        // Act
        viewModel.obtainEvent(
            RegistrationEvent.RegistrationButtonClicked(name, surname, login, password, passwordRepeat)
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first { it is RegistrationState.Main && !it.isLoading } as RegistrationState.Main
        assertEquals(RegistrationState.RegistrationError.REGISTRATION_FAILED, state.loginError)
    }

    @Test
    fun `registration with exception`() = runTest {
        // Arrange
        val name = userExample.name
        val surname = userExample.surname
        val login = userExample.login
        val password = userExample.password
        val passwordRepeat = userExample.password
        `when`(checkLoginAvailableUseCase.execute(login)).thenThrow(RuntimeException("Error"))

        // Act
        viewModel.obtainEvent(
            RegistrationEvent.RegistrationButtonClicked(name, surname, login, password, passwordRepeat)
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first { it is RegistrationState.Main && !it.isLoading } as RegistrationState.Main
        assertEquals(RegistrationState.RegistrationError.NETWORK, state.loginError)
        assertEquals(RegistrationState.RegistrationError.NETWORK, state.surnameError)
        assertEquals(RegistrationState.RegistrationError.NETWORK, state.nameError)
        assertEquals(RegistrationState.RegistrationError.NETWORK, state.passwordError)
        assertEquals(RegistrationState.RegistrationError.NETWORK, state.passwordRepeatError)
    }

    @Test
    fun `navigate to authorization`() = runTest {
        // Arrange
        val login = userExample.login
        val password = userExample.password

        // Act
        viewModel.obtainEvent(RegistrationEvent.LoginButtonClicked(login, password))
        advanceUntilIdle()

        // Assert
        val action = viewModel.action.first { it != null }
        assertEquals(RegistrationAction.NavigateToAuthorization, action)
    }

    @Test
    fun `clear viewmodel`() = runTest {
        // Act
        viewModel.obtainEvent(
            RegistrationEvent.Clear
        )
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first { it is RegistrationState.Main && !it.isLoading } as RegistrationState.Main
        assertEquals("", state.name)
        assertEquals("", state.surname)
        assertEquals("", state.login)
        assertEquals("", state.password)
        assertEquals("", state.passwordRepeat)
        val action = viewModel.action.first()
        assertEquals(null, action)
    }

    @Test
    fun `change password visibility`() = runTest {
        // Arrange
        val initialState = viewModel.state.first() as RegistrationState.Main

        // Act
        viewModel.obtainEvent(RegistrationEvent.ShowPasswordButtonClicked)
        advanceUntilIdle()

        // Assert
        val newState = viewModel.state.first() as RegistrationState.Main
        assertEquals(!initialState.passwordVisibility, newState.passwordVisibility)
    }

    @Test
    fun `change password repeat visibility`() = runTest {
        // Arrange
        val initialState = viewModel.state.first() as RegistrationState.Main

        // Act
        viewModel.obtainEvent(RegistrationEvent.ShowPasswordRepeatButtonClicked)
        advanceUntilIdle()

        // Assert
        val newState = viewModel.state.first() as RegistrationState.Main
        assertEquals(!initialState.passwordRepeatVisibility, newState.passwordRepeatVisibility)
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