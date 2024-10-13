package zinc.example.test.common.service

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import zinc.example.test.common.dto.CustomUser
import zinc.example.test.member.entity.Member
import zinc.example.test.member.repository.MemberRepository

@Service
class CustomUserDetailsService(
        private val memberRepository: MemberRepository,
        private val passwordEncoder: PasswordEncoder
): UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails =
        memberRepository.findByEmail(email)
                ?.let { createUserDetails(it) }
                ?: throw UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다.")

    private fun createUserDetails(member: Member): UserDetails =
            CustomUser(
                    member.id!!,
                    member.email,
                    passwordEncoder.encode(member.password),
                    member.memberRole!!.map { SimpleGrantedAuthority("ROLE_${it.role}") }
            )
}