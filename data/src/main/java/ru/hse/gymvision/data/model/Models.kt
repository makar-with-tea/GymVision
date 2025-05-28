package ru.hse.gymvision.data.model

import com.google.gson.annotations.SerializedName

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
    val image: String? = null
)

data class GymSchemeDTO(
    val image: String,
    val name: String,
    val clickableTrainers: List<ClickableTrainerDTO>,
    val clickableCameras: List<ClickableCameraDTO>
)

data class UserDTO(
    val name: String,
    val surname: String,
    val email: String,
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
    val name: String,
    val surname: String,
    val email: String,
    val login: String,
    val password: String
)

data class LoginRequestDTO(
    val login: String,
    val password: String,
)

data class TokenResponseDTO(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String
)

data class RefreshRequestDTO(
    val refreshToken: String
)

data class UserCheckPasswordDTO(
    val login: String,
    val password: String
)