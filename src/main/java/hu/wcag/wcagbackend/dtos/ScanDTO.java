package hu.wcag.wcagbackend.dtos;

import hu.wcag.wcagbackend.model.Scan;
import hu.wcag.wcagbackend.types.Status;

import java.time.LocalDateTime;

public record ScanDTO(
    Long Id,
    Status status,
    String websiteUrl,
    LocalDateTime scannedAt,
    LocalDateTime completedAt
) {
    public static ScanDTO fromModel (Scan scan){
        return new ScanDTO(
                scan.getId(),
                scan.getStatus(),
                scan.getWebsite().getUrl(),
                scan.getScannedAt(),
                scan.getCompletedAt()
        );
    }
}
