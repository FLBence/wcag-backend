package hu.wcag.wcagbackend.service;

import hu.wcag.wcagbackend.model.WcagError;
import hu.wcag.wcagbackend.repository.WcagErrorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * A WCAG szabályokhoz tartozó hibák adatainak kezelése.
 *
 * Felelős a hibák lekérdezéséért, valamint az adatbázisban
 * még nem szereplő WCAG hibák létrehozásáért és mentéséért.
 */
@Service
public class WcagErrorService {

    private final WcagErrorRepository errorRepository;

    public WcagErrorService(WcagErrorRepository errorRepository) {
        this.errorRepository = errorRepository;
    }

    /*
     * WCAG hiba lekérése azonosító alapján.
     *
     * Ha az adott szabályhoz még nem tartozik bejegyzés,
     * létrehozza és elmenti az új hibát a megadott adatokkal.
     */
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
}
