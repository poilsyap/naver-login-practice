package zinc.example.test.member.controller

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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import zinc.example.test.common.dto.BaseResponse
import zinc.example.test.member.dto.MemberDtoRequest
import zinc.example.test.member.service.MemberService

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

    @GetMapping("/login/naver")
    fun naverLogin(authentication: OAuth2AuthenticationToken, @RegisteredOAuth2AuthorizedClient("naver") authorizedClient: OAuth2AuthorizedClient): ResponseEntity<String> {
        val accessToken = authorizedClient.accessToken.tokenValue

        println(accessToken)

        val message: String? = memberService.naverLogin(authentication)

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.Location, "http://localhost:3000/Home?message=$message")
                .build()
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
     fun login(@RequestBody memberDtoRequest: MemberDtoRequest): ResponseEntity<String>{
        return ResponseEntity.ok("로그인 성공")
    }

    @GetMapping("/login")
    fun loginRequest(): ResponseEntity<String>{
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.Location, "http://localhost:3000")
                .build()
    }
}