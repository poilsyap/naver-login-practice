package zinc.example.test.member.controller

import com.fasterxml.jackson.databind.ser.Serializers.Base
import io.ktor.http.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zinc.example.test.common.authority.TokenInfo
import zinc.example.test.common.dto.BaseResponse
import zinc.example.test.member.dto.LoginDto
import zinc.example.test.member.dto.MemberDtoRequest
import zinc.example.test.member.entity.Member
import zinc.example.test.member.service.MemberService
import java.util.concurrent.ConcurrentHashMap

@RequestMapping("/api/member")
@RestController
class MemberController (
        private val memberService: MemberService
){

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    fun signUp(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): BaseResponse<Unit>{
        val resultMsg: String = memberService.signUp(memberDtoRequest)
        return BaseResponse(message = resultMsg)
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    fun login(@RequestBody @Valid loginDto: LoginDto): BaseResponse<TokenInfo>{
        val tokenInfo = memberService.login(loginDto)
        return BaseResponse(data = tokenInfo)
    }

    /**
     * 네이버 로그인 Oauth 연동
     */
    @GetMapping("/login/naver")
    fun naverLogin(authentication: OAuth2AuthenticationToken, @RegisteredOAuth2AuthorizedClient("naver") authorizedClient: OAuth2AuthorizedClient): ResponseEntity<String> {
        val member : Member = memberService.naverLogin(authentication, authorizedClient)

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.Location, "http://localhost:3000/Home?message=${member.email}")
                .build()
    }

    @PostMapping("/logout/naver")
    fun naverLogout(@RequestBody memberDtoRequest: MemberDtoRequest): ResponseEntity<String>{
        memberService.naverLogout(memberDtoRequest)
        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @GetMapping("/login")
    fun loginRequest(): ResponseEntity<String>{
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.Location, "http://localhost:3000")
                .build()
    }
}