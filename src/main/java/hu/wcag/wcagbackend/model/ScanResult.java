package hu.wcag.wcagbackend.model;

import hu.wcag.wcagbackend.types.ErrorLevel;
import jakarta.persistence.*;

@Entity
@Table(name = "scan_result")
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

    public ScanResult() {
    }

    public ScanResult(Long id, Scan scan, WcagError wcagError, ErrorLevel errorLevel, String htmlElement, String targetSelector, String aiSuggestion) {
        this.id = id;
        this.scan = scan;
        this.wcagError = wcagError;
        this.errorLevel = errorLevel;
        this.htmlElement = htmlElement;
        this.targetSelector = targetSelector;
        this.aiSuggestion = aiSuggestion;
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

    public ErrorLevel getErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(ErrorLevel errorLevel) {
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

    public String getAiSuggestion() {
        return aiSuggestion;
    }

    public void setAiSuggestion(String aiSuggestion) {
        this.aiSuggestion = aiSuggestion;
    }
}