package zinc.example.test.member.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
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
    fun signUp(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): ResponseEntity<String>{
        return ResponseEntity.ok(memberService.signUp(memberDtoRequest))
    }

    @GetMapping("/login/naver")
    fun naverLogin(@RequestParam code: String,
                   @RequestParam state: String): RedirectView {

        val message: String? = memberService.naverLogin(code, state)
        val redirectView = RedirectView("http://localhost:3000/Home")
        redirectView.addStaticAttribute("message", message!!)
        return redirectView
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
     fun login(@RequestBody memberDtoRequest: MemberDtoRequest): ResponseEntity<String>{
        return ResponseEntity.ok("로그인 성공")
    }
}