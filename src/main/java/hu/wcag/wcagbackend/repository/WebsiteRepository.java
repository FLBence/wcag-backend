package hu.wcag.wcagbackend.repository;

import hu.wcag.wcagbackend.model.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
* A viszgált weboldalak adatbázis műveleteinek és lekérdezéseinek kezelése.
*/
@Repository
public interface WebsiteRepository extends JpaRepository<Website, Long> {

    Optional<Website> findByUrl(String websiteUrl);
}
