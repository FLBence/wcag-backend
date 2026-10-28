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

    public WebsiteService(WebsiteRepository websiteRepository) {
        this.websiteRepository = websiteRepository;
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
