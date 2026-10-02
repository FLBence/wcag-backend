package hu.wcag.wcagbackend.dtos;

import hu.wcag.wcagbackend.model.ScanResult;

public record ScanResultDTO(
        Long Id,
        String targetSelector,
        String htmlElement,
        String errorLevel,
        String aiSuggestion,
        String ruleId,
        String errorName,
        String description,
        String wcagTags
) {
    public static ScanResultDTO fromModel (ScanResult scanResult){
        var error = scanResult.getWcagError();
        return new ScanResultDTO(
                scanResult.getId(),
                scanResult.getTargetSelector(),
                scanResult.getHtmlElement(),
                scanResult.getErrorLevel(),
                scanResult.getAiSuggestion(),
                error != null ? error.getRuleId() : null,
                error != null ? error.getName() : null,
                error != null ? error.getDescription() : null,
                error != null ? error.getWcagCriterion() : null
        );
    }
}
