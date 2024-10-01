package zinc.example.test.common.config

import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties.Registration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.stereotype.Component
import java.security.Provider


enum class NaverOAuth2Provider{
    NAVER{
        override fun getBuilder(registrationId: String) =
                getBuilder(registrationId, org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_POST, DEFAULT_LOGIN_REDIRECT_URL)
                        .scope("profile")
                        .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                        .tokenUri("https://nid.naver.com/oauth2.0/token") // 접근 토큰 발급 요청 url
                        .userInfoUri("https://openapi.naver.com/v1/nid/me") // 회원 프로필 조회 API
                        .userNameAttributeName("response")
                        .clientName("Naver")
    };

    companion object{
        val DEFAULT_LOGIN_REDIRECT_URL = "{baseUrl}/login/oauth2/code/{registrationId}"
    }

    protected fun getBuilder(registrationId: String, method: ClientAuthenticationMethod, redirectUri: String) =
            ClientRegistration.withRegistrationId(registrationId)
                    .clientAuthenticationMethod(method)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri(redirectUri)

    abstract fun getBuilder(registrationId: String): ClientRegistration.Builder
}

/**
 * api 키 매핑을 위한 프로퍼티 클래스
 */

@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client")
class NaverOAuth2ClientProperties{

    lateinit var registration: Map<String, Registration>

    companion object{
        class Registration{
            lateinit var clientId: String
            var clientSecret: String = "default"
            val jwkSetUri: String = "default"
        }

    }
}