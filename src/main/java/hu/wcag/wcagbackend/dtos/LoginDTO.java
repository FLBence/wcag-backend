package hu.wcag.wcagbackend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record LoginDTO(
        @Email @Size(min = 10, max = 40)String email,
        @Size(min = 5, max = 50)String password
) {}
