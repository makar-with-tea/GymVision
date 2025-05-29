package ru.hse.gymvision.data

import okhttp3.Interceptor
import okhttp3.Response
import ru.hse.gymvision.domain.repos.SharedPrefRepository

class AuthInterceptor(
    private val sharedPrefRepository: SharedPrefRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPrefRepository.getToken()
        val request = chain.request().newBuilder()
        if (!token.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(request.build())
    }
}
