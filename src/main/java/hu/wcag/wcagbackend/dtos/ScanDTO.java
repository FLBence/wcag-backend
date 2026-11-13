package hu.wcag.wcagbackend.dtos;

import hu.wcag.wcagbackend.model.Scan;
import hu.wcag.wcagbackend.types.Status;

import java.time.LocalDateTime;

/*
* WCAG vizsgálat adatainak továbbítása a kliens felé.
*/
public record ScanDTO(
    Long id,
    Status status,
    String websiteUrl,
    LocalDateTime scannedAt,
    LocalDateTime completedAt
) {
    /*
    * Scan entitás átalakítása ScanDTO objektummá.
    */
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
