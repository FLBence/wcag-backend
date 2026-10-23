package hu.wcag.wcagbackend.dtos;

public record RegisterDTO(
    String username,
    String email,
    String password
) {}
