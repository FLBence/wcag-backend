package hu.wcag.wcagbackend.service;

import hu.wcag.wcagbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/*
 * Egyedi felhasználóbetöltő szolgáltatás a Spring Security számára.
 *
 * Az e-mail-cím alapján megkeresi a felhasználót az adatbázisban,
 * és visszaadja a hozzá tartozó UserDetails objektumot a hitelesítési folyamat számára.
 *
 * Felhasznált források:
 *  https://stackoverflow.com/questions/76632398/spring-boot-security-use-roles
 *  https://medium.com/@victoronu/implementing-jwt-authentication-in-a-simple-spring-boot-application-with-java-b3135dbdb17b
 */
@Component
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /*
    * Felhasználó lekérése email cím alapján.
    */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nem található felhasználó ezzel az email címmel: " + email));
    }
}
