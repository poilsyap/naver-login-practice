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
        val id: String, // "id":"jssTGdshWwgmY-Uj2mc-kbW7-84MRiAOYzlN7MrPeLI"
        val gender: String, // "gender":"F"
        val email: String, // "email":"leeah9737@naver.com"
        val name: String, // "name":"\uc774\uc544\uc5f0"
        val birthday: String // "birthday":"07-10"
)
