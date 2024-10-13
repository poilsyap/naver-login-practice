package zinc.example.test.common.authority

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import zinc.example.test.common.dto.CustomUser
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.time.Duration
import java.time.ZonedDateTime
import java.util.Date
import javax.crypto.SecretKey

const val EXPIRATION_MILLISECONDS: Long = 1000 * 60 * 30

@Component
class JwtTokenProvider {
    @Value("\${jwt.secret}")
    lateinit var secretKey: String

    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)) }

    /**
     * Token 생성
     */
    fun createToken(authentication: Authentication): TokenInfo{
        val authorities: String = authentication
                .authorities
                .joinToString( ",", transform = GrantedAuthority::getAuthority )

        val now = Date()
        val accessExpiration = Date(now.time + EXPIRATION_MILLISECONDS)

        // Access Token
        val accessToken = Jwts
                .builder()
//                .setSubject(authentication.name) // 사용자 이름 설정
                .subject(authentication.name) // 사용자 이름 설정
                .claim("auth", authorities) // 권한 설정
                .claim("userId", (authentication.principal as CustomUser).userId)
//                .setIssuedAt(Date.from(now.toInstant())) // 발행 시간
                .issuedAt(now) // 발행 시간
//                .setExpiration(Date.from(accessExpiration.toInstant())) // 만료 시간
                .expiration(accessExpiration) // 만료 시간
//                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘 및 키 설정
                .signWith(key) // 서명 알고리즘 및 키 설정
                .compact()

        println("TOKEN 생성 [ Bearer " + accessToken + " ]")
        return TokenInfo("Bearer", accessToken)
    }

    /**
     * Token 정보 추출
     */
    fun getAuthentication(token: String): Authentication{
        val claims: Claims = getClaims(token)

        val auth = claims["auth"] ?:throw RuntimeException("잘못된 토큰입니다.")
        val userId = claims["userId"] ?:throw RuntimeException("잘못된 토큰입니다.")

        // 권한 정보 추출
        val authrities: Collection<GrantedAuthority> = (auth as String)
                .split(",")
                .map { SimpleGrantedAuthority(it) }

        val principal: UserDetails =
                CustomUser(userId.toString().toLong(), claims.subject, "", authrities)

        return UsernamePasswordAuthenticationToken(principal, "", authrities)
    }

    /**
     * Token 검증
     */
    fun validateToken(token: String): Boolean{
        try{
            getClaims(token)
            return true
        }catch (e: Exception){
            when(e){
                is SecurityException -> {} // Invalid JWT Token
                is MalformedJwtException -> {} // Invalid JWT Token
                is ExpiredJwtException -> {} // Expired JWT Token
                is UnsupportedJwtException -> {} // Unsupported JWT Token
                is IllegalArgumentException -> {} // JWT claims string is empty
                else -> {} // else
            }
            println(e.message)
        }
        return false
    }

    private fun getClaims(token: String): Claims =
            Jwts.parser()
//                    .setSigningKey(key)
                    .verifyWith(key)
                    .build()
//                    .parseClaimsJws(token)
                    .parseSignedClaims(token)
                    .payload

}