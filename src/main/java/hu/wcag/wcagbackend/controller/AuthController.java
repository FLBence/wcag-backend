package hu.wcag.wcagbackend.controller;

import hu.wcag.wcagbackend.dtos.RegisterDTO;
import hu.wcag.wcagbackend.model.User;
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

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDTO registerDTO){
        User user = new User();
        user.setUsername(registerDTO.username());
        user.setEmail(registerDTO.email());
        user.setPassword(passwordEncoder.encode(registerDTO.password()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("A felhasználó regisztrációja sikeresen megtörtént!");    }
}
