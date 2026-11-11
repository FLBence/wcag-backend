package hu.wcag.wcagbackend.controller;

import hu.wcag.wcagbackend.dtos.ScanDTO;
import hu.wcag.wcagbackend.model.User;
import hu.wcag.wcagbackend.repository.ScanRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
public class UserController {

    private final ScanRepository scanRepository;

    public UserController(ScanRepository scanRepository) {
        this.scanRepository = scanRepository;
    }

    @GetMapping("/myscans")
    public ResponseEntity<List<ScanDTO>> getMyScans(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        List<ScanDTO> myScans = scanRepository.findByUserIdOrderByCompletedAtDesc(user.getId())
                .stream()
                .map(ScanDTO::fromModel)
                .toList();

        return ResponseEntity.ok(myScans);
    }
}
