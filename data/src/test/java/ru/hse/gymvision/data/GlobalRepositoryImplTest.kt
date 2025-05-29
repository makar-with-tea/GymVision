package ru.hse.gymvision.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import retrofit2.HttpException
import ru.hse.gymvision.data.api.GlobalApiService
import ru.hse.gymvision.data.model.CameraInfoDTO
import ru.hse.gymvision.data.model.GymInfoDTO
import ru.hse.gymvision.data.model.GymSchemeDTO
import ru.hse.gymvision.data.model.LoginRequestDTO
import ru.hse.gymvision.data.model.RegisterRequestDTO
import ru.hse.gymvision.data.model.RotateInfoDTO
import ru.hse.gymvision.data.model.StreamInfoDTO
import ru.hse.gymvision.data.model.TokenResponseDTO
import ru.hse.gymvision.data.model.UserCheckPasswordDTO
import ru.hse.gymvision.data.model.UserDTO
import ru.hse.gymvision.data.model.ZoomInfoDTO
import ru.hse.gymvision.domain.exception.CameraInUseException
import ru.hse.gymvision.domain.exception.InvalidCredentialsException
import ru.hse.gymvision.domain.exception.LoginAlreadyInUseException
import ru.hse.gymvision.domain.model.GymInfoModel
import ru.hse.gymvision.domain.model.TokenModel
import ru.hse.gymvision.domain.model.UserModel

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class GlobalRepositoryImplTest {
    private lateinit var repository: GlobalRepositoryImpl
    private lateinit var apiService: GlobalApiService

    @Before
    fun setUp() {
        apiService = mock()
        repository = GlobalRepositoryImpl(apiService)
    }

    @Test
    fun `getGymList should return gym list`() = runTest {
        // Arrange
        val gymList = listOf(
            GymInfoDTO(1, "Gym 1", "Address 1", null),
            GymInfoDTO(2, "Gym 2", "Address 2", null)
        )
        whenever(apiService.getGymList()).thenReturn(gymList)

        // Act
        val result = repository.getGymList()
        advanceUntilIdle()

        // Assert
        verify(apiService).getGymList()
        assertEquals(
            listOf(
                GymInfoModel(1, "Gym 1", "Address 1", null),
                GymInfoModel(2, "Gym 2", "Address 2", null)
            ),
            result
        )
    }

    @Test
    fun `getUserInfo should return user info`() = runTest {
        // Arrange
        val userDTO = UserDTO("John", "Doe", "johndoe@example.com", "johndoe", "password")
        whenever(apiService.getUserInfo("johndoe")).thenReturn(userDTO)

        // Act
        val result = repository.getUserInfo("johndoe")
        advanceUntilIdle()

        // Assert
        verify(apiService).getUserInfo("johndoe")
        assertEquals(
            UserModel("John", "Doe", "johndoe@example.com", "johndoe"),
            result
        )
    }

    @Test
    fun `getUserInfo should return null for non-existent user`() = runTest {
        // Arrange
        whenever(apiService.getUserInfo("nonexistent")).thenReturn(null)

        // Act
        val result = repository.getUserInfo("nonexistent")
        advanceUntilIdle()

        // Assert
        verify(apiService).getUserInfo("nonexistent")
        assertNull(result)
    }

    @Test
    fun `register should call apiService with correct parameters`() = runTest {
        // Arrange
        val name = "John"
        val surname = "Doe"
        val email = "johndoe@example.com"
        val login = "johndoe"
        val password = "password"
        val dto = RegisterRequestDTO(name, surname, email, login, password)
        whenever(apiService.register(dto)).thenReturn(TokenResponseDTO("access_token", "refresh_token"))

        // Act
        val result = repository.register(name, surname, email, login, password)
        advanceUntilIdle()

        // Assert
        verify(apiService).register(dto)
        assertEquals(
            TokenModel("access_token", "refresh_token"),
            result
        )
    }

    @Test
    fun `deleteUser should call apiService with correct login`() = runTest {
        // Arrange
        val login = "johndoe"
        whenever(apiService.deleteUser(login)).thenReturn(mapOf("success" to true))

        // Act
        repository.deleteUser(login)
        advanceUntilIdle()

        // Assert
        verify(apiService).deleteUser(login)
    }

    @Test
    fun `getGymScheme should return gym scheme`() = runTest {
        // Arrange
        val gymId = 1
        val gymSchemeDTO = GymSchemeDTO(
            image = "base64image",
            name = "Gym Scheme",
            clickableTrainers = emptyList(),
            clickableCameras = emptyList()
        )
        whenever(apiService.getGymScheme(gymId)).thenReturn(gymSchemeDTO)

        // Act
        val result = repository.getGymScheme(gymId)
        advanceUntilIdle()

        // Assert
        verify(apiService).getGymScheme(gymId)
        assertEquals("Gym Scheme", result?.name)
    }

    @Test
    fun `getGymScheme should return null for 404 error`() = runTest {
        // Arrange
        val gymId = 1
        val exception = mock<HttpException> {
            on { code() } doReturn 404
        }
        whenever(apiService.getGymScheme(gymId)).thenThrow(exception)

        // Act
        val result = repository.getGymScheme(gymId)
        advanceUntilIdle()

        // Assert
        verify(apiService).getGymScheme(gymId)
        assertNull(result)
    }

    @Test
    fun `getGymScheme should throw for non-404 error`() = runTest {
        // Arrange
        val gymId = 1
        whenever(apiService.getGymScheme(gymId)).thenThrow(httpException)

        // Act & Assert
        assertThrows(HttpException::class.java) {
            runBlocking {
                repository.getGymScheme(gymId)
                advanceUntilIdle()
            }
        }
        verify(apiService).getGymScheme(gymId)
    }

    @Test
    fun `login should return token model`() = runTest {
        // Arrange
        val login = "johndoe"
        val password = "password"
        val tokenResponse = TokenResponseDTO("access_token", "refresh_token")
        whenever(apiService.login(LoginRequestDTO(login, password))).thenReturn(tokenResponse)

        // Act
        val result = repository.login(login, password)
        advanceUntilIdle()

        // Assert
        verify(apiService).login(LoginRequestDTO(login, password))
        assertEquals(TokenModel("access_token", "refresh_token"), result)
    }

    @Test
    fun `login should throw InvalidCredentialsException for 401 error`() = runTest {
        // Arrange
        val login = "johndoe"
        val password = "wrongpassword"
        val exception = mock<HttpException> {
            on { code() } doReturn 401
        }
        whenever(apiService.login(LoginRequestDTO(login, password))).thenThrow(exception)

        // Act & Assert
        assertThrows(
            InvalidCredentialsException::class.java
        ) {
            runBlocking {
                repository.login(login, password)
                advanceUntilIdle()
            }
        }
        verify(apiService).login(LoginRequestDTO(login, password))
    }

    @Test
    fun `updateUser should call apiService with correct parameters`() = runTest {
        // Arrange
        val login = "johndoe"
        val name = "John"
        val surname = "Doe"
        val email = "johndoe@example.com"
        val password = "password"

        // Act
        repository.updateUser(name, surname, email, login, password)
        advanceUntilIdle()

        // Assert
        verify(apiService).updateUser(login, name, surname, email, password)
    }

    @Test
    fun `updateUser should propagate API error`() = runTest {
        // Arrange
        val login = "johndoe"
        val name = "John"
        val surname = "Doe"
        val email = "johndoe@example.com"
        val password = "password"
        whenever(apiService.updateUser(login, name, surname, email, password))
            .thenThrow(httpException)

        // Act & Assert
        assertThrows(HttpException::class.java) {
            runBlocking {
                repository.updateUser(name, surname, email, login, password)
                advanceUntilIdle()
            }
        }
        verify(apiService).updateUser(login, name, surname, email, password)
    }

    @Test
    fun `startStream should return stream URL`() = runTest {
        // Arrange
        val cameraId = 1
        val aiEnabled = true
        val streamInfo = StreamInfoDTO("http://stream.url")
        whenever(apiService.startStream(CameraInfoDTO(cameraId, aiEnabled))).thenReturn(streamInfo)

        // Act
        val result = repository.startStream(cameraId, aiEnabled)
        advanceUntilIdle()

        // Assert
        verify(apiService).startStream(CameraInfoDTO(cameraId, aiEnabled))
        assertEquals("http://stream.url", result)
    }

    @Test
    fun `startStream should throw CameraInUseException for 409 error`() = runTest {
        // Arrange
        val cameraId = 1
        val aiEnabled = true
        val exception = mock<HttpException> {
            on { code() } doReturn 409
        }
        whenever(apiService.startStream(CameraInfoDTO(cameraId, aiEnabled)))
            .thenThrow(exception)

        // Act & Assert
        assertThrows(CameraInUseException::class.java) {
            runBlocking {
                repository.startStream(cameraId, aiEnabled)
                advanceUntilIdle()
            }
        }
        verify(apiService).startStream(CameraInfoDTO(cameraId, aiEnabled))
    }

    @Test
    fun `stopStream should call apiService with correct parameters`() = runTest {
        // Arrange
        val cameraId = 1

        // Act
        repository.stopStream(cameraId)

        // Assert
        verify(apiService).stopStream(CameraInfoDTO(cameraId, aiEnabled = false))
    }

    @Test
    fun `moveCamera should call apiService with correct parameters`() = runTest {
        // Arrange
        val cameraId = 1
        val rotateX = 10f
        val rotateY = 20f

        // Act
        repository.moveCamera(cameraId, rotateX, rotateY)

        // Assert
        verify(apiService).moveCamera(RotateInfoDTO(cameraId, rotateX, rotateY))
    }

    @Test
    fun `stopMove should call apiService with correct parameters`() = runTest {
        // Arrange
        val cameraId = 1

        // Act
        repository.stopMove(cameraId)

        // Assert
        verify(apiService).stopMove(CameraInfoDTO(cameraId, aiEnabled = false))
    }

    @Test
    fun `zoomCamera should call apiService with correct parameters`() = runTest {
        // Arrange
        val cameraId = 1
        val zoomLevel = 2.5f

        // Act
        repository.zoomCamera(cameraId, zoomLevel)

        // Assert
        verify(apiService).zoomCamera(ZoomInfoDTO(cameraId, zoomLevel))
    }

    @Test
    fun `stopZoom should call apiService with correct parameters`() = runTest {
        // Arrange
        val cameraId = 1

        // Act
        repository.stopZoom(cameraId)

        // Assert
        verify(apiService).stopZoom(CameraInfoDTO(cameraId, aiEnabled = false))
    }

    @Test
    fun `checkCameraAccessibility should return true when camera is accessible`() = runTest {
        // Arrange
        val cameraId = 1

        // Act
        val result = repository.checkCameraAccessibility(cameraId)

        // Assert
        verify(apiService).startStream(CameraInfoDTO(cameraId, aiEnabled = false))
        verify(apiService).stopStream(CameraInfoDTO(cameraId, aiEnabled = false))
        assertEquals(true, result)
    }

    @Test
    fun `checkCameraAccessibility should return false when camera is in use`() = runTest {
        // Arrange
        val cameraId = 1
        val exception = mock<HttpException> {
            on { code() } doReturn 409
        }
        whenever(apiService.startStream(CameraInfoDTO(cameraId, aiEnabled = false)))
            .thenThrow(exception)

        // Act
        val result = repository.checkCameraAccessibility(cameraId)

        // Assert
        verify(apiService).startStream(CameraInfoDTO(cameraId, aiEnabled = false))
        assertEquals(false, result)
    }

    @Test
    fun `checkPassword should return true when API response is successful`() = runTest {
        // Arrange
        val login = "johndoe"
        val password = "password"
        whenever(apiService.checkPassword(UserCheckPasswordDTO(login, password)))
            .thenReturn(mapOf("success" to true))

        // Act
        val result = repository.checkPassword(login, password)

        // Assert
        verify(apiService).checkPassword(UserCheckPasswordDTO(login, password))
        assertEquals(true, result)
    }

    @Test
    fun `checkPassword should return false when API response is missing success key`() = runTest {
        // Arrange
        val login = "johndoe"
        val password = "password"
        whenever(apiService.checkPassword(UserCheckPasswordDTO(login, password)))
            .thenReturn(emptyMap())

        // Act
        val result = repository.checkPassword(login, password)

        // Assert
        verify(apiService).checkPassword(UserCheckPasswordDTO(login, password))
        assertEquals(false, result)
    }

    @Test
    fun `register should return token model on success`() = runTest {
        // Arrange
        val name = "John"
        val surname = "Doe"
        val email = "johndoe@example.com"
        val login = "johndoe"
        val password = "password"
        val tokenResponse = TokenResponseDTO("access_token", "refresh_token")
        whenever(apiService.register(RegisterRequestDTO(name, surname, email, login, password)))
            .thenReturn(tokenResponse)

        // Act
        val result = repository.register(name, surname, email, login, password)

        // Assert
        verify(apiService).register(RegisterRequestDTO(name, surname, email, login, password))
        assertEquals(TokenModel("access_token", "refresh_token"), result)
    }

    @Test
    fun `register should throw LoginAlreadyInUseException for special error`() = runTest {
        // Arrange
        val name = "John"
        val surname = "Doe"
        val email = "johndoe@example.com"
        val login = "johndoe"
        val password = "password"
        val exception = mock<HttpException> {
            on { code() } doReturn 400
            on { message() } doReturn "Login already in use"
        }
        whenever(apiService.register(RegisterRequestDTO(name, surname, email, login, password)))
            .thenThrow(exception)

        // Act & Assert
        assertThrows(LoginAlreadyInUseException::class.java) {
            runBlocking {
                repository.register(name, surname, email, login, password)
            }
        }
        verify(apiService).register(RegisterRequestDTO(name, surname, email, login, password))
    }

    @Test
    fun `register should propagate general API error`() = runTest {
        // Arrange
        val name = "John"
        val surname = "Doe"
        val email = "johndoe@example.com"
        val login = "johndoe"
        val password = "password"
        whenever(apiService.register(RegisterRequestDTO(name, surname, email, login, password)))
            .thenThrow(httpException)

        // Act & Assert
        assertThrows(HttpException::class.java) {
            runBlocking {
                repository.register(name, surname, email, login, password)
            }
        }
        verify(apiService).register(RegisterRequestDTO(name, surname, email, login, password))
    }

    @Test
    fun `getUserInfo should return null for 404 error`() = runTest {
        // Arrange
        val login = "nonexistent"
        val exception = mock<HttpException> { on { code() } doReturn 404 }
        whenever(apiService.getUserInfo(login)).thenThrow(exception)

        // Act
        val result = repository.getUserInfo(login)

        // Assert
        verify(apiService).getUserInfo(login)
        assertNull(result)
    }

    @Test
    fun `getUserInfo should throw for non-404 error`() = runTest {
        // Arrange
        val login = "johndoe"
        val exception = mock<HttpException> { on { code() } doReturn 500 }
        whenever(apiService.getUserInfo(login)).thenThrow(exception)

        // Act & Assert
        assertThrows(HttpException::class.java) {
            runBlocking {
                repository.getUserInfo(login)
            }
        }
        verify(apiService).getUserInfo(login)
    }

    @Test
    fun `login should propagate general API error`() = runTest {
        // Arrange
        val login = "johndoe"
        val password = "password"
        val exception = mock<HttpException> { on { code() } doReturn 500 }
        whenever(apiService.login(LoginRequestDTO(login, password))).thenThrow(exception)

        // Act & Assert
        assertThrows(HttpException::class.java) {
            runBlocking {
                repository.login(login, password)
            }
        }
        verify(apiService).login(LoginRequestDTO(login, password))
    }

    @Test
    fun `startStream should propagate general API error`() = runTest {
        // Arrange
        val cameraId = 1
        val aiEnabled = true
        val exception = mock<HttpException> { on { code() } doReturn 500 }
        whenever(apiService.startStream(CameraInfoDTO(cameraId, aiEnabled))).thenThrow(exception)

        // Act & Assert
        assertThrows(HttpException::class.java) {
            runBlocking {
                repository.startStream(cameraId, aiEnabled)
            }
        }
        verify(apiService).startStream(CameraInfoDTO(cameraId, aiEnabled))
    }

    @Test
    fun `checkCameraAccessibility should propagate general API error`() = runTest {
        // Arrange
        val cameraId = 1
        val exception = mock<HttpException> { on { code() } doReturn 500 }
        whenever(apiService.startStream(CameraInfoDTO(cameraId, aiEnabled = false))).thenThrow(exception)

        // Act & Assert
        assertThrows(HttpException::class.java) {
            runBlocking {
                repository.checkCameraAccessibility(cameraId)
            }
        }
        verify(apiService).startStream(CameraInfoDTO(cameraId, aiEnabled = false))
    }

    companion object {
        private val httpException = mock<HttpException> {
            on { code() } doReturn 500
        }
    }
}
