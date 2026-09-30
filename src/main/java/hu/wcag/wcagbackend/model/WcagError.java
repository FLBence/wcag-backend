package hu.wcag.wcagbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "errors")
public class WcagError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_id", nullable = false, unique = true)
    private String ruleId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "wcag_criterion", length = 50)
    private String wcagCriterion;

    public WcagError() {
    }

    public WcagError(Long id, String ruleId, String name, String description, String wcagCriterion) {
        this.id = id;
        this.ruleId = ruleId;
        this.name = name;
        this.description = description;
        this.wcagCriterion = wcagCriterion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWcagCriterion() {
        return wcagCriterion;
    }

    public void setWcagCriterion(String wcagCriterion) {
        this.wcagCriterion = wcagCriterion;
    }
}