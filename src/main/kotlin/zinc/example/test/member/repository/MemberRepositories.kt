package zinc.example.test.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import zinc.example.test.member.entity.Member

interface MemberRepository : JpaRepository<Member, Long>{
    fun findByLoginId(loginId: String): Member?
    fun findByEmail(email: String): Member?
}