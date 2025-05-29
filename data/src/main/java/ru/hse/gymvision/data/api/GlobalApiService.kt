package ru.hse.gymvision.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import ru.hse.gymvision.data.model.CameraInfoDTO
import ru.hse.gymvision.data.model.GymInfoDTO
import ru.hse.gymvision.data.model.GymSchemeDTO
import ru.hse.gymvision.data.model.LoginRequestDTO
import ru.hse.gymvision.data.model.RefreshRequestDTO
import ru.hse.gymvision.data.model.RegisterRequestDTO
import ru.hse.gymvision.data.model.RotateInfoDTO
import ru.hse.gymvision.data.model.StreamInfoDTO
import ru.hse.gymvision.data.model.TokenResponseDTO
import ru.hse.gymvision.data.model.UserCheckPasswordDTO
import ru.hse.gymvision.data.model.UserDTO
import ru.hse.gymvision.data.model.ZoomInfoDTO

interface GlobalApiService {
    @GET("global/gyms")
    suspend fun getGymList(): List<GymInfoDTO>

    @GET("global/gyms/{gym_id}/scheme")
    suspend fun getGymScheme(@Path("gym_id") gymId: Int): GymSchemeDTO?

    @GET("global/users/{login}")
    suspend fun getUserInfo(@Path("login") login: String): UserDTO?

    @POST("global/users/check_password")
    suspend fun checkPassword(@Body user: UserCheckPasswordDTO): Map<String, Boolean>

    @PUT("global/users/{login}")
    suspend fun updateUser(
        @Path("login") login: String,
        @Query("name") name: String?,
        @Query("surname") surname: String?,
        @Query("email") email: String?,
        @Query("password") password: String?
    ): Map<String, Boolean>

    @DELETE("global/users/{login}")
    suspend fun deleteUser(@Path("login") login: String): Map<String, Boolean>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDTO): TokenResponseDTO

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDTO): TokenResponseDTO

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequestDTO): TokenResponseDTO

    @POST("streams/start")
    suspend fun startStream(@Body cameraInfo: CameraInfoDTO): StreamInfoDTO

    @POST("streams/stop")
    suspend fun stopStream(@Body cameraInfo: CameraInfoDTO)

    @POST("streams/move")
    suspend fun moveCamera(@Body rotateInfo: RotateInfoDTO)

    @POST("streams/stop_move")
    suspend fun stopMove(@Body cameraInfo: CameraInfoDTO)

    @POST("streams/zoom")
    suspend fun zoomCamera(@Body zoomInfo: ZoomInfoDTO)

    @POST("streams/stop_zoom")
    suspend fun stopZoom(@Body cameraInfo: CameraInfoDTO)
}
