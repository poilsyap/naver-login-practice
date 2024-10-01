package zinc.example.test.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig {

    /** React와 Spring Boot는 다른 포트에서 동작하기 때문에 CORS 설정 **/
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000", "*") // React 앱이 실행되는 포트 -- "http://localhost:3000" 추후 적용
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Authorization", "Cache-Control", "Content-Type")
            }
        }
    }
}