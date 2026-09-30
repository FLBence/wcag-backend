package hu.wcag.wcagbackend.repository;

import hu.wcag.wcagbackend.model.WcagError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WcagErrorRepository extends JpaRepository<WcagError, Long> {

    Optional<WcagError> findByRuleId(String ruleId);

    boolean existsByRuleId(String ruleId);
}
