package hu.wcag.wcagbackend.service;

import hu.wcag.wcagbackend.dtos.LoginDTO;
import hu.wcag.wcagbackend.dtos.RegisterDTO;
import hu.wcag.wcagbackend.model.User;
import hu.wcag.wcagbackend.repository.UserRepository;
import hu.wcag.wcagbackend.types.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/*
 * A felhasználókhoz kapcsolódó műveletek kezelése.
 *
 * Felelős a felhasználók lekérdezéséért, regisztrációjáért,
 * bejelentkezésének ellenőrzéséért és jelszavuk módosításáért.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * Felhasználó lekérése azonosító alapján.
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("A felhasználó nem található ezzel az ID-val: " + id));
    }

    /*
     * Felhasználó lekérése e-mail-cím alapján.
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("A felhasználó nem található ezzel az email címmel: " + email));
    }

    /*
     * Új felhasználó regisztrálása.
     *
     * Ellenőrzi, hogy az e-mail-cím szerepel-e már az adatbázisban,
     * majd titkosítja a jelszót, beállítja a felhasználó alapadatait és elmenti az új felhasználót.
     */
    @Transactional
    public void registerUser(RegisterDTO registerDTO){
        if (userRepository.existsByEmail(registerDTO.email())){
            throw new IllegalArgumentException("Ez az email cím már regisztrálva van: " + registerDTO.email());
        }
        User user = new User();
        user.setUsername(registerDTO.username());
        user.setEmail(registerDTO.email());
        user.setPassword(passwordEncoder.encode(registerDTO.password()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        try {
            userRepository.save(user);
        }catch (Exception e){
            throw new IllegalArgumentException("Ez az email cím már regisztrálva van: " + registerDTO.email());
        }
    }

    /*
     * A felhasználó bejelentkezési adatainak ellenőrzése.
     *
     * Megkeresi a felhasználót az e-mail-címe alapján, majd összehasonlítja a megadott jelszót
     * az adatbázisban tárolt titkosított jelszóval.
     */
    @Transactional(readOnly = true)
    public void login(LoginDTO loginDTO){
        User user = getUserByEmail(loginDTO.email());
        if(!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            throw new RuntimeException("Hibás jelszó!");
        }
    }

    /*
     * A felhasználó jelszavának módosítása e-mail-cím alapján.
     *
     * Az új jelszót titkosítva menti az adatbázisba.
     */
    @Transactional
    public void updatePasswordByEmail(LoginDTO loginDTO){
        User user = userRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new RuntimeException("A felhasználó nem található ezzel az email címmel: " + loginDTO.email()));
        user.setPassword(passwordEncoder.encode(loginDTO.password()));
        userRepository.save(user);
    }
}
