package zinc.example.test.member.service

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import jakarta.transaction.Transactional
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import zinc.example.test.common.dto.NaverApiResponse
import zinc.example.test.common.dto.NaverOAuthToken
import zinc.example.test.common.exception.InvalidInputException
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
    fun naverLogin(authentication: OAuth2AuthenticationToken): String{

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
            println("회원 정보 존재")
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

            return email
    }


}