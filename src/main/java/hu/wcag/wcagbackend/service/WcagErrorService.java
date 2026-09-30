package hu.wcag.wcagbackend.service;

import hu.wcag.wcagbackend.model.WcagError;
import hu.wcag.wcagbackend.repository.WcagErrorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WcagErrorService {

    private final WcagErrorRepository errorRepository;

    public WcagErrorService(WcagErrorRepository errorRepository) {
        this.errorRepository = errorRepository;
    }

    @Transactional
    public WcagError getOrCreateError(String ruleId, String name, String description, String wcagCriterion) {
        return errorRepository.findByRuleId(ruleId)
                .orElseGet(() -> {
                    WcagError wcagError = new WcagError();
                    wcagError.setRuleId(ruleId);
                    wcagError.setName(name);
                    wcagError.setDescription(description);
                    wcagError.setWcagCriterion(wcagCriterion);
                    return errorRepository.save(wcagError);
                });
    }

    @Transactional(readOnly = true)
    public List<WcagError> getAllErrors() {
        return errorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public  WcagError getErrorByRuleId(String ruleId) {
        return errorRepository.findByRuleId(ruleId)
                .orElseThrow(() -> new RuntimeException("WCAG hiba nem található ezzel a szabály azonosítóval: " + ruleId));
    }
}
