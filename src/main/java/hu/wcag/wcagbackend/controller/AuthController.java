package hu.wcag.wcagbackend.controller;

import hu.wcag.wcagbackend.dtos.LoginDTO;
import hu.wcag.wcagbackend.dtos.RegisterDTO;
import hu.wcag.wcagbackend.model.User;
import hu.wcag.wcagbackend.service.JwtService;
import hu.wcag.wcagbackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/*
* Felhasználói hitelesítéshez kapcsolódó API végpontok beállítása és kezelése.
*
* Regisztráció, bejelentkezés és jelszómódosítás végponjainak kezelése.
*/
@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService){
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterDTO registerDTO){
        userService.registerUser(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Sikeres regisztráció!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            userService.login(loginDTO);
            Map<String, Object> response = new HashMap<>();
            User user = userService.getUserByEmail(loginDTO.email());
            String token = jwtService.generateToken(user.getEmail());
            response.put("username", user.getUsername());
            response.put("userId", user.getId());
            response.put("token", token);
            response.put("message", "Sikeres bejelentkezés");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PutMapping("/changepass")
    public ResponseEntity<String> changePassword(@RequestBody LoginDTO loginDTO){
        try{
            userService.updatePasswordByEmail(loginDTO);
            return ResponseEntity.status(HttpStatus.OK).body("A jelszó módosítása sikeresen megtörtént!");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
