package hu.wcag.wcagbackend.service;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/*
* JWT alapú ellenőrzéshez használt token kezelése.
*
* Felelős a JWT token létrehozásáért, token érvényesség ellenőrzéséért, valamint a tokenből származó felhasználói adatok kiolvasásáért.
*
* Felhasznált források:
*   https://stackoverflow.com/questions/76632398/spring-boot-security-use-roles
*   https://medium.com/@victoronu/implementing-jwt-authentication-in-a-simple-spring-boot-application-with-java-b3135dbdb17b
*
* A kód a felhasznált források és a mesterséges intelligencia segítségével készültek.
*/
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    private SecretKey key;

    /*
    * JWT-hez szükséges kulcs inicializálása az alkalmazás konfigurációjában megadott érték alapján.
    */
    @PostConstruct
    protected void init(){
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /*
    * Új JWT token létrehozása a mgeadott email címhez.
    * Token lejárati idejének beállítása, valamint digitális aláírása a korábban inicializált kulccsal.
    */
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, Jwts.SIG.HS384)
                .compact();
    }

    /*
    * Email cím koilvasása a JWT tokenból a token validálása után.
    *
    */
    public String getEmailFromToken(String token){
        return Jwts.parser()
                .verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /*
    * JWT token érvényességének ellenőrzése.
    *
    * Ellenőrzi a token aláírását, szerkezetét, valamint lejárati idejét, csak érvényes token esetén ad true értéket vissza.
    * */
    public boolean validateJwtToken(String token) {
        try{
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        }catch (SecurityException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
}
