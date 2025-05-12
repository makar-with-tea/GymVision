package ru.hse.gymvision.data

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.hse.gymvision.data.api.GlobalApiService
import ru.hse.gymvision.data.model.RefreshRequestDTO
import ru.hse.gymvision.domain.repos.SharedPrefRepository

class TokenAuthenticator(
    private val sharedPrefRepository: SharedPrefRepository,
    private val apiService: GlobalApiService
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = sharedPrefRepository.getRefreshToken() ?: return null

        val newToken = try {
            runBlocking {
                apiService.refreshToken(RefreshRequestDTO(refreshToken)).refreshToken
            }
        } catch (e: Exception) {
            return null
        }

        sharedPrefRepository.saveToken(newToken)

        return response.request().newBuilder()
            .header("Authorization", "Bearer $newToken")
            .build()
    }
}
