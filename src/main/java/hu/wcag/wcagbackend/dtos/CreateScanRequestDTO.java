package hu.wcag.wcagbackend.dtos;

import jakarta.validation.constraints.NotBlank;

public record CreateScanRequestDTO(
        @NotBlank(message = "Az URL nem lehet üres!")
        String websiteUrl
) {}
