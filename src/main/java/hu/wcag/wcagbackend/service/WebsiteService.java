package hu.wcag.wcagbackend.service;


import hu.wcag.wcagbackend.model.User;
import hu.wcag.wcagbackend.model.Website;
import hu.wcag.wcagbackend.repository.UserRepository;
import hu.wcag.wcagbackend.repository.WebsiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WebsiteService {

    private final WebsiteRepository websiteRepository;
    private final UserRepository userRepository;

    public WebsiteService(WebsiteRepository websiteRepository, UserRepository userRepository) {
        this.websiteRepository = websiteRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Website addWebsiteToUser(Long userId, Website website) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("A felhasználó nem található ezzel az ID-val: " + userId));

        if (websiteRepository.exisrsByUrlAndUserId(website.getUrl(), userId)) {
            throw new IllegalArgumentException("Ez a weboldal URL már hozzá van adva ehhez a fiókhoz!");
        }
        website.setUser(user);
        return websiteRepository.save(website);
    }

    @Transactional(readOnly = true)
    public List<Website> getWebsitesByUserId(Long userId) {
        return websiteRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Website getWebsiteById(Long id) {
        return websiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("A weboldal nem található ezzel az ID-val: " + id));
    }

    @Transactional
    public void deleteWebsite(Long id){
        if (!websiteRepository.existsById(id))
            throw new RuntimeException("A törlendő weboldal nem található ezzel az ID-val: " + id);
        websiteRepository.deleteById(id);
    }
}
