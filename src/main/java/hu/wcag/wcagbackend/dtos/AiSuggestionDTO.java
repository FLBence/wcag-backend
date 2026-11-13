package hu.wcag.wcagbackend.dtos;

import java.util.List;

/*
* AI alapú javítási javaslat adatainak továbbítása a kliens felé.
*/
public record AiSuggestionDTO(
        String summary,
        List<String> stepsToFix,
        String codeExample
) {}
