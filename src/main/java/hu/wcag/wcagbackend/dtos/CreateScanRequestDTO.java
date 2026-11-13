package hu.wcag.wcagbackend.dtos;

import jakarta.validation.constraints.NotBlank;

/*
* Új WCAG vizsgálat létrehozásához szükséges adatok tárolása.
*/
public record CreateScanRequestDTO(
        @NotBlank(message = "Az URL nem lehet üres!")
        String websiteUrl,
        Long id
) {}
