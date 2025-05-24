package ru.hse.gymvision.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import ru.hse.gymvision.data.api.GlobalApiService
import ru.hse.gymvision.data.model.CameraInfoDTO
import ru.hse.gymvision.data.model.GymInfoDTO
import ru.hse.gymvision.data.model.GymSchemeDTO
import ru.hse.gymvision.data.model.StreamInfoDTO
import ru.hse.gymvision.data.model.UserDTO
import ru.hse.gymvision.domain.exampledata.gymListExample
import ru.hse.gymvision.domain.exampledata.gymSchemeExample
import ru.hse.gymvision.domain.exampledata.userExample
import java.time.LocalDateTime

fun createMockGlobalApiService(): GlobalApiService {
    val mockService = mock(GlobalApiService::class.java)

    runBlocking {
        `when`(mockService.getGymList()).thenReturn(
            listOf(
                GymInfoDTO(1, "Gym 1", "Address 1",null),
                GymInfoDTO(2, "Gym 1", "Address 1", null)
            )
        )
        `when`(mockService.getGymList()).thenThrow(RuntimeException("Failed to fetch gym list"))
    }

    runBlocking {
        `when`(mockService.getGymScheme(1)).thenReturn(
            GymSchemeDTO(
                name = "Gym A Scheme",
                image = ByteArray(0),
                clickableCameraDTOS = emptyList(),
                clickableTrainerDTOS = emptyList()
            )
        )
        `when`(mockService.getGymScheme(-1)).thenThrow(RuntimeException("Invalid gym id"))
    }

    runBlocking {
        `when`(mockService.getUserInfo("validUser")).thenReturn(
            UserDTO("John", "Doe", "johndoe", "password1")
        )
        `when`(mockService.getUserInfo("invalidLogin"))
            .thenThrow(RuntimeException("User not found"))
    }

    runBlocking {
        `when`(mockService.login(
            UserDTO("John", "Doe", "johndoe", "password123"))
        ).thenReturn(
            mapOf("success" to true)
        )
        `when`(mockService.login(
            UserDTO("Some", "One", "invalidLogin", "badPassword1"))
        ).thenThrow(
            RuntimeException("Registration failed")
        )
    }

    runBlocking {
        `when`(mockService.register(
            UserDTO("John", "Doe", "johndoe", "password1"))
        ).thenReturn(
            mapOf("success" to true)
        )
        `when`(mockService.register(
            UserDTO("John", "Doe", "bodydouble", "password1"))
        ).thenThrow(
            RuntimeException("User already exists")
        )
    }

    runBlocking {
        `when`(mockService.updateUser(
            "johndoe", "Jo", "Doe", "password1")
        ).thenReturn(mapOf("success" to true))
        `when`(mockService.updateUser(
            "invalidLogin", null, null, null)
        ).thenThrow(RuntimeException("User not found"))
    }

    runBlocking {
        `when`(mockService.deleteUser("johndoe")).thenReturn(mapOf("success" to true))
        `when`(mockService.deleteUser("invalidLogin"))
            .thenThrow(RuntimeException("User not found"))
    }

    runBlocking {
        `when`(mockService.getCameras()).thenReturn(
            listOf(CameraInfoDTO(1), CameraInfoDTO(2))
        )
        `when`(mockService.getCameras()).thenThrow(RuntimeException("Failed to fetch cameras"))
    }

    runBlocking {
        `when`(mockService.startStream(CameraInfoDTO(1))).thenReturn(
            StreamInfoDTO("http://example.com/stream1", 0)
        )
        `when`(mockService.startStream(CameraInfoDTO(-1))).thenThrow(
            RuntimeException("Invalid camera ID")
        )
    }

    runBlocking {
        `when`(mockService.stopStream(CameraInfoDTO(1))).thenReturn(
            mapOf("status" to "stopped")
        )
        `when`(mockService.stopStream(CameraInfoDTO(-1))).thenThrow(
            RuntimeException("Invalid camera ID")
        )
    }

    runBlocking {
        `when`(
            mockService.getStats(
                "validSession",
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
            )
        ).thenReturn(mapOf("stat1" to 100, "stat2" to 200))
        `when`(
            mockService.getStats(
                "invalidSession",
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
            )
        ).thenThrow(RuntimeException("Invalid session ID"))
    }

    return mockService
}

class GlobalRepositoryImplTest {

    private lateinit var underTest: GlobalRepositoryImpl
    private val globalApiService = createMockGlobalApiService()

    @Before
    fun setUp() {
        underTest = GlobalRepositoryImpl(
//            globalApiService
        )
    }

    @Test
    fun `getGymList should return example gym list`() = runBlocking {
        val result = underTest.getGymList()
        assertEquals(gymListExample, result)
    }

    @Test
    fun `getGymScheme should return example gym scheme for valid id`() = runBlocking {
        val result = underTest.getGymScheme(1)
        assertEquals(gymSchemeExample, result)
    }

    @Test
    fun `getGymScheme should return null for invalid id`() = runBlocking {
        val result = underTest.getGymScheme(-1)
        assertNull(result)
    }

    @Test
    fun `getUserInfo should return userExample for matching login`() = runBlocking {
        val result = underTest.getUserInfo(userExample.login)
        assertEquals(userExample, result)
    }

    @Test
    fun `getUserInfo should return null for non-matching login`() = runBlocking {
        val result = underTest.getUserInfo("nonexistent")
        assertNull(result)
    }

    @Test(expected = Exception::class)
    fun `login should throw exception`(): Unit = runBlocking {
        underTest.login("test", "test")
    }

    @Test
    fun `register should update userExample`() = runBlocking {
        val name = "John"
        val surname = "Doe"
        val login = "johndoe"
        val password = "password"

        val result = underTest.register(name, surname, login, password)
        assertTrue(result)
        assertEquals(name, userExample.name)
        assertEquals(surname, userExample.surname)
        assertEquals(login, userExample.login)
        assertEquals(password, userExample.password)
    }

    @Test
    fun `updateUser should modify userExample fields`() = runBlocking {
        val newName = "UpdatedName"
        val newSurname = "UpdatedSurname"
        val newPassword = "UpdatedPassword"

        underTest.updateUser(newName, newSurname, userExample.login, newPassword)

        assertEquals(newName, userExample.name)
        assertEquals(newSurname, userExample.surname)
        assertEquals(newPassword, userExample.password)
    }

    @Test
    fun `deleteUser should reset userExample`() = runBlocking {
        underTest.deleteUser(userExample.login)

        assertEquals("", userExample.name)
        assertEquals("", userExample.surname)
        assertEquals("", userExample.login)
        assertEquals("", userExample.password)
    }

    @Test
    fun `checkLoginAvailable should return true for available login`() = runBlocking {
        val result = underTest.checkLoginAvailable("newlogin")
        assertTrue(result)
    }

    @Test
    fun `checkLoginAvailable should return false for existing login`() = runBlocking {
        val result = underTest.checkLoginAvailable(userExample.login)
        assertFalse(result)
    }

    @Test
    fun `checkCameraAccessibility should return random boolean`() = runBlocking {
        val result = underTest.checkCameraAccessibility(1, 1)
        assertNotNull(result)
    }

    @Test
    fun `getCameraLink should return valid URL`() = runBlocking {
        val result = underTest.getCameraLink(1, 1)
        assertEquals(
            "https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4",
            result
        )
    }
}