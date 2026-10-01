package hu.wcag.wcagbackend.repository;

import hu.wcag.wcagbackend.model.Scan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScanRepository extends JpaRepository<Scan, Long> {

    @Query("SELECT s FROM Scan s JOIN FETCH s.website WHERE s.id = :id")
    Optional<Scan> findByIdWithWebsite(@Param("id") Long id);

    List<Scan> findByWebsiteIdOrderByScannedAtDesc(Long id);

    List<Scan> findByStatus(String status);
}
