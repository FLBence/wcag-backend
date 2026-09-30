package hu.wcag.wcagbackend.repository;

import hu.wcag.wcagbackend.model.Scan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanRepository extends JpaRepository<Scan, Long> {

    List<Scan> findByWebsiteIdOrderByScanDateDesc(Long id);

    List<Scan> findByStatus(String status);
}
