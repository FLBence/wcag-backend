package hu.wcag.wcagbackend.dtos;

import hu.wcag.wcagbackend.model.User;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String username,
        String email,
        LocalDateTime createdAt
) {
    public static UserDTO fromModel(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
