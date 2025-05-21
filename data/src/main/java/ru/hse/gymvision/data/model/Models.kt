package ru.hse.gymvision.data.model

data class ClickableCameraDTO(
    val id: Int,
    val xPercent: Float,
    val yPercent: Float
)

data class ClickableTrainerDTO(
    val id: Int,
    val name: String,
    val description: String,
    val xPercent: Float,
    val yPercent: Float,
    val widthPercent: Float,
    val heightPercent: Float
)

data class GymInfoDTO(
    val id: Int,
    val name: String,
    val address: String,
    val image: ByteArray? = null
)

data class GymSchemeDTO(
    val image: ByteArray,
    val name: String,
    val clickableTrainerDTOS: List<ClickableTrainerDTO>,
    val clickableCameraDTOS: List<ClickableCameraDTO>
)

data class UserDTO(
    val name: String,
    val surname: String,
    val login: String,
    val password: String
)

data class CameraInfoDTO(
    val cameraId: Int
)

data class StreamInfoDTO(
    val streamUrl: String,
    val startedAt: Long
)

data class RegisterRequestDTO(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequestDTO(
    val username: String,
    val password: String
)

data class TokenResponseDTO(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long
)

data class RefreshRequestDTO(
    val refreshToken: String
)