package hu.wcag.wcagbackend.security;

import hu.wcag.wcagbackend.service.CustomUserDetailService;
import hu.wcag.wcagbackend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 * JWT alapú hitelesítés a beérkező HTTP kéréseknél a Spring Security számára.
 *
 * Felhasznált források:
 *   https://docs.spring.io/spring-security/reference/servlet/architecture.html
 *   https://medium.com/@victoronu/implementing-jwt-authentication-in-a-simple-spring-boot-application-with-java-b3135dbdb17b
 *   https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html
 *   https://stackoverflow.com/questions/76632398/spring-boot-security-use-roles
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailService customUserDetailService;
    private final SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailService customUserDetailService) {
        this.jwtService = jwtService;
        this.customUserDetailService = customUserDetailService;
    }

    /*
    * A beérkező HTTP kérés Authorization fejlécéből kiolvassa a JWT token-t, majd validálja azt,
    *  amennyiben sikeres volt az ellenőrzés, akkor beállítja a felhasználó hitelesítesi adatait a Spring Security SecurityContext objektumába.
    *
    * Mesterséges intelligenciával módosíott kódrészlet, a módosítások a felhasznált források kódjainak feladatspecifikus átalakításaval történtek.
    */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7).trim();

                if (jwtService.validateJwtToken(jwt)) {
                    String userEmail = jwtService.getEmailFromToken(jwt);

                    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = this.customUserDetailService.loadUserByUsername(userEmail);

                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(authToken);
                        SecurityContextHolder.setContext(context);
                        securityContextRepository.saveContext(context, request, response);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Hitelesítési hiba a filterben: ", e);
        }

        filterChain.doFilter(request, response);
    }
}