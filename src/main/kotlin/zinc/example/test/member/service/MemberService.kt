package zinc.example.test.member.service

import com.nimbusds.jose.shaded.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import jakarta.transaction.Transactional
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import zinc.example.test.common.dto.NaverApiResponse
import zinc.example.test.common.dto.NaverOAuthToken
import zinc.example.test.common.status.Gender
import zinc.example.test.member.dto.MemberDtoRequest
import zinc.example.test.member.entity.Member
import zinc.example.test.member.repository.MemberRepository
import java.lang.RuntimeException
import java.time.LocalDate
import java.time.MonthDay
import java.time.format.DateTimeFormatter

@Transactional
@Service
class MemberService (
        private val memberRepository: MemberRepository
){
    /**
     * 회원가입
     */
    fun signUp(memberDtoRequest: MemberDtoRequest): String{
        // 1. id 중복 검사
        var member: Member? =
                memberRepository.findByLoginId(memberDtoRequest.loginId)
        if(member != null){
            return "존재하는 회원 ID 입니다."
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
    fun naverLogin(code : String, state: String): String{

        // 접근 토큰 발급 요청 url
        var requestUrl = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&client_id=CW0BQSe6XFSmQf39fG3V&client_secret=rllk1SzcR6&code=$code&state=$state"
        val client = HttpClient(CIO){
            install(ContentNegotiation){
                json()
            }
        }

        var accessToken: String
        runBlocking {
            val response: HttpResponse = client.get(requestUrl)

            if(response.status.value == 200){
                val naverToken : NaverOAuthToken = Json.decodeFromString(response.bodyAsText())
                accessToken = naverToken.access_token
            }else{
                throw RuntimeException()
            }
        }

        // 회원 프로필 조회 API
        val profileInfoUrl = "https://openapi.naver.com/v1/nid/me"
        runBlocking {

            val response: HttpResponse = client.get(profileInfoUrl){
                headers{
                    append("Authorization", "Bearer " + accessToken)
                }
            }

            if(response.status.value == 200){
                val userInfo : NaverApiResponse = Json.decodeFromString(response.bodyAsText())
                println(userInfo.toString())

                var member: Member? =
                        memberRepository.findByLoginId(userInfo.response.id)

                if(member != null){
                    println("회원 정보 존재")
                    return@runBlocking
                }else{
                    println("존재하는 회원 정보가 없어서 회원가입 진행")
                    val birth : MonthDay = MonthDay.parse(userInfo.response.birthday, DateTimeFormatter.ofPattern("MM-dd"))
                    val gen : Gender = if(userInfo.response.birthday == "F"){
                        Gender.WOMAN
                    }else{
                        Gender.MAN
                    }

                    member = Member(
                            null,
                            userInfo.response.id,
                                    "naver-11",
                            userInfo.response.name,
                            birth,
                            gen,
                            userInfo.response.email
                    )

                    memberRepository.save(member)
                    println("회원가입 완료")
                }
            }else{
                throw RuntimeException()
            }
        }

        println("==== naver login end ====")
        return "네이버 로그인 성공"
    }



}