package ru.hse.gymvision.data.api

import retrofit2.http.*
import ru.hse.gymvision.data.model.*
import java.time.LocalDateTime

interface GlobalApiService {
    @GET("gyms")
    suspend fun getGymList(): List<GymInfoDTO>

    @GET("gyms/{gym_id}/scheme")
    suspend fun getGymScheme(@Path("gym_id") gymId: Int): GymSchemeDTO?

    @GET("users/{login}")
    suspend fun getUserInfo(@Path("login") login: String): UserDTO?

    @POST("users/login")
    suspend fun login(@Body user: UserDTO): Map<String, Boolean>

    @POST("users/register")
    suspend fun register(@Body user: UserDTO): Map<String, Boolean>

    @PUT("users/{login}")
    suspend fun updateUser(
        @Path("login") login: String,
        @Query("name") name: String?,
        @Query("surname") surname: String?,
        @Query("password") password: String?
    ): Map<String, Boolean>

    @DELETE("users/{login}")
    suspend fun deleteUser(@Path("login") login: String): Map<String, Boolean>

    @POST("register")
    suspend fun registerToken(@Body request: RegisterRequestDTO): TokenResponseDTO

    @POST("login")
    suspend fun loginToken(@Body request: LoginRequestDTO): TokenResponseDTO

    @POST("refresh")
    suspend fun refreshToken(@Body request: RefreshRequestDTO): TokenResponseDTO

    @GET("cameras")
    suspend fun getCameras(): List<CameraInfoDTO>

    @POST("start")
    suspend fun startStream(@Body cameraInfoDTO: CameraInfoDTO): StreamInfoDTO

    @POST("stop")
    suspend fun stopStream(@Body cameraInfoDTO: CameraInfoDTO): Map<String, String>

    @GET("stats")
    suspend fun getStats(
        @Query("session_id") sessionId: String,
        @Query("from_ts") fromTs: LocalDateTime,
        @Query("to_ts") toTs: LocalDateTime
    ): Map<String, Any>
}