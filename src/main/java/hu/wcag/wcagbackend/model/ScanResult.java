package hu.wcag.wcagbackend.model;

import hu.wcag.wcagbackend.dtos.AiSuggestionDTO;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "scan_results")
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

    @Column(name = "error_level", nullable = false)
    private String errorLevel;

    @Column(name = "html_element", nullable = false, columnDefinition = "TEXT")
    private String htmlElement;

    @Column(name = "target_selector", nullable = false, columnDefinition = "TEXT")
    private String targetSelector;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ai_suggestion", columnDefinition = "jsonb")
    private AiSuggestionDTO aiSuggestion;

    @Column(name = "failure_summary", columnDefinition = "TEXT")
    private String failureSummary;

    public ScanResult() {
    }

    public ScanResult(Long id, Scan scan, WcagError wcagError, String errorLevel, String htmlElement, String targetSelector, AiSuggestionDTO aiSuggestion, String failureSummary) {
        this.id = id;
        this.scan = scan;
        this.wcagError = wcagError;
        this.errorLevel = errorLevel;
        this.htmlElement = htmlElement;
        this.targetSelector = targetSelector;
        this.aiSuggestion = aiSuggestion;
        this.failureSummary = failureSummary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Scan getScan() {
        return scan;
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }

    public WcagError getWcagError() {
        return wcagError;
    }

    public void setWcagError(WcagError wcagError) {
        this.wcagError = wcagError;
    }

    public String getErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(String errorLevel) {
        this.errorLevel = errorLevel;
    }

    public String getHtmlElement() {
        return htmlElement;
    }

    public void setHtmlElement(String htmlElement) {
        this.htmlElement = htmlElement;
    }

    public String getTargetSelector() {
        return targetSelector;
    }

    public void setTargetSelector(String targetSelector) {
        this.targetSelector = targetSelector;
    }

    public AiSuggestionDTO getAiSuggestion() {
        return aiSuggestion;
    }

    public void setAiSuggestion(AiSuggestionDTO aiSuggestion) {
        this.aiSuggestion = aiSuggestion;
    }

    public String getFailureSummary() {return failureSummary;}

    public void setFailureSummary(String failureSummary) {
        this.failureSummary = failureSummary;
    }
}