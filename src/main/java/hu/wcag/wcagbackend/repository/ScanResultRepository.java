package hu.wcag.wcagbackend.repository;

import hu.wcag.wcagbackend.model.ScanResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {

    List<ScanResult> findByScanId(Long scanId);

    List<ScanResult> findByScanIdAndWcagErrorId(Long scanId, Long errorId);
}
