package hu.wcag.wcagbackend.config;

import hu.wcag.wcagbackend.security.JwtAuthenticationFilter;
import hu.wcag.wcagbackend.service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
/*
* Spring Security konfiguráció
*
* Hozzáférés szabályozás és a JWT alapú jogosultságkezelés, hitelesítés beállítása.
* Felhasznált források:
*   https://docs.spring.io/spring-security/reference/servlet/architecture.html
*   https://medium.com/@victoronu/implementing-jwt-authentication-in-a-simple-spring-boot-application-with-java-b3135dbdb17b
*   https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html
*   https://stackoverflow.com/questions/76632398/spring-boot-security-use-roles
*/
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter authenticationFilter;
    private final CustomUserDetailService userDetailService;
    private final CorsConfig corsConfig;

    public SecurityConfig(JwtAuthenticationFilter authenticationFilter, CustomUserDetailService userDetailService, CorsConfig corsConfig){
        this.authenticationFilter = authenticationFilter;
        this.userDetailService = userDetailService;
        this.corsConfig = corsConfig;
    }

    /*
    * Engedélyezett végpontok beállítása, JWT hitelesítési szűrő és munkamenet kezelés.
    *
    * Mesterséges intelligenciával módosított kódrészlet, a módosítások a felhasznált források kódjainak feladatspecifikus átalakításaval történtek.
    */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    /*
    * Autentikációs szolgáltató létrehozása
    *
    * Felhasználó hitelesítését a CustomUserDetailService, valamint a BCrypt végzi.
    *
     * Mesterséges intelligenciával módosított kódrészlet, a módosítások a felhasznált források kódjainak feladatspecifikus átalakításaval történtek.
    */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    /*
    * AuthenticationManager példány lekérése
    */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /*
    * BCrypt alapú jelszótitkosító létrehozása a jelszavak biztonságos tárolása érdekében
    */
    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
}
