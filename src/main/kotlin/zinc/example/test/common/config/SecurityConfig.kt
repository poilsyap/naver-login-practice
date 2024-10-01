package zinc.example.test.common.config

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.web.filter.CharacterEncodingFilter
import zinc.example.test.member.service.MemberService

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
                .authorizeHttpRequests{
                    authz -> authz
                        .requestMatchers("/","/h2-console/**", "/api/member/**").permitAll()
                        .requestMatchers(PathRequest.toH2Console()).permitAll() // h2-console 허용
                        .anyRequest().authenticated()
                }
                .oauth2Login{
                    oauth -> oauth
//                        .loginPage("/api/member/login/naver")
                        .defaultSuccessUrl("/api/member/login/naver", true)
                        .failureUrl("http://localhost:3000/")
                }
                .exceptionHandling {
                    per -> per
                        .authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/api/member/login"))
                }

                .addFilterAt(CharacterEncodingFilter(), CsrfFilter::class.java)
                .csrf {csrf -> csrf.ignoringRequestMatchers("/api/member/**") // 포스트맨 로그인 요청을 허용하기 위해 해당 경로 허용 csrf.disable() 사용해서 전체 허용해줘도 됨
                                .ignoringRequestMatchers(PathRequest.toH2Console())} // h2-console 허용 -- csrf.ignoringRequestMatchers(PathRequest.toH2Console())
                .headers{headers -> headers.frameOptions{ // h2-console 허용
                    frameOptions -> frameOptions.sameOrigin()
                }}

        return http.build()
    }

    @Bean
    fun clientRegistrationRepository(oAuth2ClientProperties: OAuth2ClientProperties,
            naverOAuth2ClientProperties: NaverOAuth2ClientProperties): InMemoryClientRegistrationRepository{

        val registrations = oAuth2ClientProperties.registration.keys
                .map{ getRegistration(oAuth2ClientProperties, it) }
                .filter { it != null }
                .toMutableList()

        val plusRegistrations = naverOAuth2ClientProperties.registration

        for(plusRegistration in plusRegistrations){

            when(plusRegistration.key){
                "naver" -> registrations.add(NaverOAuth2Provider.NAVER.getBuilder("naver")
                        .clientId(plusRegistration.value.clientId)
                        .clientSecret(plusRegistration.value.clientSecret)
                        .jwkSetUri(plusRegistration.value.jwkSetUri)
                        .build())
            }
        }

        return InMemoryClientRegistrationRepository(registrations)
    }

    private fun getRegistration(clientProperties: OAuth2ClientProperties, client: String): ClientRegistration?{
        val registration = clientProperties.registration[client]
        return when(client){
            "google" -> CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(registration?.clientId)
                    .clientSecret(registration?.clientSecret)
                    .scope("email", "profile")
                    .build()
            else -> null
        }


    }



}