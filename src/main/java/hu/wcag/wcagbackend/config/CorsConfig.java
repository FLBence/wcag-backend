package hu.wcag.wcagbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/*
* CORS konfiguráció beállítása
* Felhasznált forrás: https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html
*
* Mesterséges intelligenciával kibővített kódrészlet, a módosítások a felhasznált források kódjainak feladatspecifikus átalakításaval történtek.
*/
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> allowedOrigins = new java.util.ArrayList<>();
        allowedOrigins.add("http://localhost:4200");
        allowedOrigins.add("https://wcag-accessibility-auditor.netlify.app");
        configuration.setAllowedOrigins(allowedOrigins);

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
