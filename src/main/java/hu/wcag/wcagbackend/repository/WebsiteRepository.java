package hu.wcag.wcagbackend.repository;

import hu.wcag.wcagbackend.model.User;
import hu.wcag.wcagbackend.model.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebsiteRepository extends JpaRepository<Website, Long> {

    List<Website> findByUserId(Long userId);

    boolean existsByUrlAndUserId(String url, Long userId);

    Optional<Website> findByUrl(String websiteUrl);

    Long user(User user);
}
