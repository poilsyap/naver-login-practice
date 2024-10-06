package zinc.example.test.member.entity

import jakarta.persistence.*
import zinc.example.test.common.status.Gender
import zinc.example.test.common.status.ROLE
import java.time.LocalDate
import java.time.MonthDay

@Entity
@Table(
        uniqueConstraints = [UniqueConstraint(name = "uk_member_email", columnNames = ["email"])]
)
class Member(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        val id: Long? = null,

        @Column(nullable = false, length = 50, updatable = false)
        val loginId: String,

        @Column(nullable = false, length = 100)
        val password: String,

        @Column(nullable = false, length = 10)
        val name: String,

        @Column(nullable = false)
        @Temporal(TemporalType.DATE)
        val birthDate: LocalDate,

        @Column(nullable = false, length = 5)
        @Enumerated(EnumType.STRING)
        val gender: Gender,

        @Column(nullable = false, length = 30)
        val email: String,
){
        @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
        val memberRole: List<MemberRole>? = null
}

@Entity
class MemberRole(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        var id: Long? = null,

        @Column(nullable = false, length = 30)
        @Enumerated(EnumType.STRING)
        val role: ROLE,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(foreignKey = ForeignKey(name = "fk_user_role_member_id"))
        val member: Member,
)