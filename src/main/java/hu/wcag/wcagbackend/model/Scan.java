package hu.wcag.wcagbackend.model;

import hu.wcag.wcagbackend.types.Role;
import hu.wcag.wcagbackend.types.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="scans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "website_id", nullable = false)
    private Website website;
    @Column(name = "scanned_at", nullable = false, updatable = false)
    private LocalDateTime scannedAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.PENDING;

    @PrePersist
    protected void onCreate() {
        this.scannedAt = LocalDateTime.now();
        if (this.status == null)
            this.status = Status.PENDING;
    }
}
