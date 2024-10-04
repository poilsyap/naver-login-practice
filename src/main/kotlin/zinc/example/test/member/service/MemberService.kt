package zinc.example.test.member.service

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import jakarta.transaction.Transactional
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import zinc.example.test.common.config.NaverOAuth2ClientProperties
import zinc.example.test.common.exception.InvalidInputException
import zinc.example.test.common.status.Gender
import zinc.example.test.member.dto.MemberDtoRequest
import zinc.example.test.member.entity.Member
import zinc.example.test.member.repository.MemberRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

@Transactional
@Service
class MemberService (
        private val memberRepository: MemberRepository,
        private val naverOAuth2ClientProperties: NaverOAuth2ClientProperties
){

    // auccess token 정보를 메모리에 저장 --> @TODO : 나중에 클래스 생성해서 관리하는 것으로 변경할 예정
    var tokenMap = ConcurrentHashMap<String, String>()

    /**
     * 회원가입
     */
    fun signUp(memberDtoRequest: MemberDtoRequest): String{
        // 1. id 중복 검사
        var member: Member? =
                memberRepository.findByEmail(memberDtoRequest.email)

        if(member != null){
            throw InvalidInputException("eamil", "이미 등록된 Email 입니다.")
        }

        member = Member(
                null,
                memberDtoRequest.loginId,
                memberDtoRequest.password,
                memberDtoRequest.name,
                memberDtoRequest.birthDate,
                memberDtoRequest.gender,
                memberDtoRequest.email
        )

        memberRepository.save(member)

        return "회원가입 성공!"
    }

    /**
     * 네이버 로그인
     */
    fun naverLogin(authentication: OAuth2AuthenticationToken, authorizedClient: OAuth2AuthorizedClient): String{

        val userAttributes = authentication.principal.attributes
        val response = userAttributes["response"] as Map<String, Any>

        val id = response["id"] as String
        val gender = response["gender"] as String
        val email = response["email"] as String
        val name = response["name"] as String
        val birthday = response["birthday"] as String
        val birthyear = response["birthyear"] as String


        var member: Member? =
                memberRepository.findByEmail(email!!)

        if(member != null) {
            println("회원 정보 존재 로그인 진행")
        }else {
            println("존재하는 회원 정보가 없어서 회원가입 진행")

            val birth : LocalDate = LocalDate.parse(birthyear!! + "-" + birthday!!, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val gen : Gender = if(gender == "F"){
                Gender.WOMAN
            }else{
                Gender.MAN
            }

            member = Member(
                    null,
                    id!!,
                    "naver-11",
                    name!!,
                    birth,
                    gen!!,
                    email!!
            )

            memberRepository.save(member)
            println("회원가입 완료")
        }

        tokenMap.put(email, authorizedClient.accessToken.tokenValue) // token 정보 map 에 저장
        return email
    }

    /**
     * 네이버 로그아웃
     */
    fun naverLogout(memberDtoRequest: MemberDtoRequest): String{

        val userEmail : String? = memberDtoRequest.email

        if(userEmail == null) {
            throw InvalidInputException("eamil", "등록된 사용자 토큰이 없습니다.")
        }

        val accessToken : String? = tokenMap.get(userEmail)
        val (clientId, clientSecret) = getNaverClientInfo();

        val client = HttpClient(CIO)
        runBlocking {
            val response: HttpResponse = client.get("https://nid.naver.com/oauth2.0/token"){
                parameter("grant_type", "delete")
                parameter("client_id", clientId)
                parameter("client_secret", clientSecret)
                parameter("access_token", accessToken)
                parameter("service_provider", "NAVER")
            }

            if(response.status.value === 200){
                println("로그아웃 성공 --> tokenMap 에서 해당 키에 저장된 토큰 값을 삭제")
                tokenMap.remove(userEmail)
            }else{
                throw InvalidInputException("loggout", "로그아웃 실패!")
            }
        }

        return "네이버 로그아웃 성공"
    }

    /**
     * 네이버 Oauth 연동 필요 키값을 가져오는 로직
     */
    fun getNaverClientInfo(): Pair<String, String>{
        val naverRegistration = naverOAuth2ClientProperties.registration["naver"]
        return if(naverRegistration != null){
            Pair(naverRegistration.clientId, naverRegistration.clientSecret)
        }else{
            throw InvalidInputException("info", "naver registration info is null")
        }
    }

}