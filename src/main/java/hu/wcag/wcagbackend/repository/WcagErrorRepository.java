package hu.wcag.wcagbackend.repository;

import hu.wcag.wcagbackend.model.WcagError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * A WCAG-vizsgálatok során talált hibák adatbázis műveleteinek és lekérdezéseinek kezelése.
 */
@Repository
public interface WcagErrorRepository extends JpaRepository<WcagError, Long> {

    Optional<WcagError> findByRuleId(String ruleId);
}
