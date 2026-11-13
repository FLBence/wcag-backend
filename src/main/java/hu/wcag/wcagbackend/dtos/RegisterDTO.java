package hu.wcag.wcagbackend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/*
 * Regisztrációhoz szükséges adatok tárolása, egyedi a mezőkre vozatkozó validációs feltétellek.
 */
public record RegisterDTO(
        @Size(min = 5, max = 50) String username,
        @Email @Size(min = 10, max = 40)String email,
        @Size(min = 5, max = 50)String password
) {}
