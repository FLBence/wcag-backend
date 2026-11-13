package hu.wcag.wcagbackend.repository;

import hu.wcag.wcagbackend.model.ScanResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * A WCAG-vizsgálatok eredményeinek adatbázis műveleteinek és lekérdezéseinek kezelése.
 */
@Repository
public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {

    List<ScanResult> findByScanId(Long scanId);

    void deleteByScanId(Long scanId);

    ScanResult getById(Long scanResultId);
}
