package hu.wcag.wcagbackend.controller;

import hu.wcag.wcagbackend.dtos.LoginDTO;
import hu.wcag.wcagbackend.dtos.RegisterDTO;
import hu.wcag.wcagbackend.model.User;
import hu.wcag.wcagbackend.repository.UserRepository;
import hu.wcag.wcagbackend.service.UserService;
import hu.wcag.wcagbackend.types.Role;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, UserRepository userRepository){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDTO registerDTO){
        if (userRepository.existsByEmail(registerDTO.email())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ez az e-mail cím már regisztrálva van!");
        }
        User user = new User();
        user.setUsername(registerDTO.username());
        user.setEmail(registerDTO.email());
        user.setPassword(passwordEncoder.encode(registerDTO.password()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("A felhasználó regisztrációja sikeresen megtörtént!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        try {
            userService.login(loginDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Sikeres bejelentkezés");
            User user = userService.getUserByEmail(loginDTO.email());
            response.put("username", user.getUsername());
            response.put("userId", user.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
