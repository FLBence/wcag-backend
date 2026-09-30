package hu.wcag.wcagbackend.model;

import hu.wcag.wcagbackend.types.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "scans")
public class Scan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "website_id", nullable = false)
    private Website website;

    @Column(name = "scanned_at", nullable = false, updatable = false)
    private LocalDateTime scannedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;

    public Scan() {
    }

    public Scan(Long id, Website website, LocalDateTime scannedAt, LocalDateTime completedAt, Status status) {
        this.id = id;
        this.website = website;
        this.scannedAt = scannedAt;
        this.completedAt = completedAt;
        this.status = status != null ? status : Status.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        this.scannedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = Status.PENDING;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Website getWebsite() {
        return website;
    }

    public void setWebsite(Website website) {
        this.website = website;
    }

    public LocalDateTime getScannedAt() {
        return scannedAt;
    }

    public void setScannedAt(LocalDateTime scannedAt) {
        this.scannedAt = scannedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}