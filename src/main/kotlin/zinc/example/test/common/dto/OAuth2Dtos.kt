package zinc.example.test.common.dto

import kotlinx.serialization.Serializable

@Serializable
data class NaverOAuthToken(
        val access_token: String,
        val refresh_token: String,
        val token_type: String,
        val expires_in: String,
)

@Serializable
data class NaverApiResponse(
        val resultcode: String,
        val message: String,
        val response: NaverApiProfileInfo,
)

@Serializable
data class NaverApiProfileInfo(
        val id: String,
        val gender: String,
        val email: String,
        val name: String,
        val birthday: String
)
