package ru.hse.gymvision.ui.authorization

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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import ru.hse.gymvision.domain.exception.InvalidCredentialsException
import ru.hse.gymvision.domain.model.UserModel
import ru.hse.gymvision.domain.usecase.user.GetPastLoginUseCase
import ru.hse.gymvision.domain.usecase.user.LoginUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorizationViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AuthorizationViewModel
    private lateinit var getPastLoginUseCase: GetPastLoginUseCase
    private lateinit var loginUseCase: LoginUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getPastLoginUseCase = mock(GetPastLoginUseCase::class.java)
        loginUseCase = mock(LoginUseCase::class.java)
        viewModel = AuthorizationViewModel(
            getPastLoginUseCase,
            loginUseCase,
            dispatcherIO = testDispatcher,
            dispatcherMain = testDispatcher
        )
    }

    @Test
    fun `successful login`() = runTest {
        // Arrange
        val login = userExample.login
        val password = PASSWORD_EXAMPLE

        // Act
        viewModel.obtainEvent(AuthorizationEvent.LoginButtonClicked(login, password))
        advanceUntilIdle()

        // Assert
        verify(loginUseCase).execute(login, password)
        val state = viewModel.state.first()
        assertTrue(state is AuthorizationState.Main)
        assertEquals((state as AuthorizationState.Main).login, login)
        assertEquals(password, state.password)
        assertEquals(AuthorizationState.AuthorizationError.IDLE, state.loginError)
        assertEquals(AuthorizationState.AuthorizationError.IDLE, state.passwordError)
        val action = viewModel.action.first()
        assertEquals(AuthorizationAction.NavigateToGymList, action)
    }

    @Test
    fun `login with empty login`() = runTest {
        // Arrange
        val login = ""
        val password = PASSWORD_EXAMPLE

        // Act
        viewModel.obtainEvent(AuthorizationEvent.LoginButtonClicked(login, password))
        advanceUntilIdle()

        // Assert
        verify(loginUseCase, never()).execute(login, password)
        val state = viewModel.state.first() as AuthorizationState.Main
        assertEquals(login, state.login)
        assertEquals(password, state.password)
        assertFalse(state.isLoading)
    }

    @Test
    fun `login with empty password`() = runTest {
        // Arrange
        val login = userExample.login
        val password = ""

        // Act
        viewModel.obtainEvent(AuthorizationEvent.LoginButtonClicked(login, password))
        advanceUntilIdle()

        // Assert
        verify(loginUseCase, never()).execute(login, password)
        val state = viewModel.state.first() as AuthorizationState.Main
        assertEquals(login, state.login)
        assertEquals(password, state.password)
        assertFalse(state.isLoading)
        assertEquals(AuthorizationState.AuthorizationError.EMPTY_PASSWORD, state.passwordError)
    }

    @Test
    fun `login with invalid credentials`() = runTest {
        // Arrange
        val login = userExample.login
        val password = "wrongPassword"
        `when`(loginUseCase.execute(login, password)).thenThrow(
            InvalidCredentialsException()
        )

        // Act
        viewModel.obtainEvent(AuthorizationEvent.LoginButtonClicked(login, password))
        advanceUntilIdle()

        // Assert
        verify(loginUseCase).execute(login, password)
        val state = viewModel.state.first() as AuthorizationState.Main
        assertEquals(login, state.login)
        assertEquals(password, state.password)
        assertFalse(state.isLoading)
        val action = viewModel.action.first()
        assertNull(action)
    }

    @Test
    fun `login with exception`() = runTest {
        // Arrange
        val login = userExample.login
        val password = PASSWORD_EXAMPLE
        `when`(loginUseCase.execute(login, password)).thenThrow(RuntimeException())

        // Act
        viewModel.obtainEvent(AuthorizationEvent.LoginButtonClicked(login, password))
        advanceUntilIdle()

        // Assert
        verify(loginUseCase).execute(login, password)
        val state = viewModel.state.first() as AuthorizationState.Main
        assertEquals(login, state.login)
        assertEquals(password, state.password)
        assertEquals(AuthorizationState.AuthorizationError.NETWORK, state.passwordError)
        assertEquals(AuthorizationState.AuthorizationError.NETWORK, state.loginError)
    }

    @Test
    fun `checkPastLogin successful`() = runTest {
        // Arrange
        val pastLogin = ru.hse.gymvision.domain.exampledata.userExample.login
        `when`(getPastLoginUseCase.execute()).thenReturn(pastLogin)

        // Act
        viewModel.obtainEvent(AuthorizationEvent.CheckPastLogin)
        advanceUntilIdle()

        // Assert
        verify(getPastLoginUseCase).execute()
        val action = viewModel.action.first()
        assertEquals(AuthorizationAction.NavigateToGymList, action)
    }

    @Test
    fun `checkPastLogin no past login`() = runTest {
        // Arrange
        `when`(getPastLoginUseCase.execute()).thenReturn(null)

        // Act
        viewModel.obtainEvent(AuthorizationEvent.CheckPastLogin)
        advanceUntilIdle()

        // Assert
        verify(getPastLoginUseCase).execute()
        val state = viewModel.state.first()
        assertTrue(state is AuthorizationState.Main)
    }

    @Test
    fun `navigate to registration`() = runTest {
        // Arrange
        val login = userExample.login
        val password = PASSWORD_EXAMPLE

        // Act
        viewModel.obtainEvent(AuthorizationEvent.RegistrationButtonClicked(login, password))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first() as AuthorizationState.Main
        assertEquals(login, state.login)
        assertEquals(password, state.password)
        val action = viewModel.action.first { it != null }
        assertEquals(AuthorizationAction.NavigateToRegistration, action)
    }

    @Test
    fun `change password visibility`() = runTest {
        // Arrange
        viewModel.obtainEvent(AuthorizationEvent.CheckPastLogin)
        advanceUntilIdle()
        val initialState = viewModel.state.first() as AuthorizationState.Main

        // Act
        viewModel.obtainEvent(AuthorizationEvent.ShowPasswordButtonClicked)
        advanceUntilIdle()

        // Assert
        val newState = viewModel.state.first() as AuthorizationState.Main
        assertEquals(!initialState.passwordVisibility, newState.passwordVisibility)
    }

    @Test
    fun `clear viewmodel`() = runTest {
        // Arrange
        viewModel.obtainEvent(AuthorizationEvent.CheckPastLogin)
        advanceUntilIdle()

        // Act
        viewModel.obtainEvent(AuthorizationEvent.Clear)
        advanceUntilIdle()

        // Assert
        viewModel.state.first() as AuthorizationState.Idle
        val action = viewModel.action.first()
        assertNull(action)
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