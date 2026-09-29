package hu.wcag.wcagbackend.model;

import hu.wcag.wcagbackend.types.ErrorLevel;
import hu.wcag.wcagbackend.types.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="scan_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scan_id", nullable = false)
    private Scan scan;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_id", nullable = false)
    private WcagError wcagError;
    @Enumerated(EnumType.STRING)
    @Column(name = "error_level", nullable = false)
    private ErrorLevel errorLevel;
    @Column(name = "html_element", nullable = false, columnDefinition = "TEXT")
    private String htmlElement;
    @Column(name = "target_selector", nullable = false, columnDefinition = "TEXT")
    private String targetSelector;
    @Column(name = "ai_suggestion", columnDefinition = "TEXT")
    private String aiSuggestion;
}
