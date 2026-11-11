package hu.wcag.wcagbackend.dtos;

import java.util.List;

public record AiSuggestionDTO(
        String summary,
        List<String> stepsToFix,
        String codeExample
) {}
