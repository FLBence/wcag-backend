package hu.wcag.wcagbackend.service;

import hu.wcag.wcagbackend.repository.WebsiteRepository;
import org.springframework.stereotype.Service;

/*
 * A rendszerben tárolt weboldalakhoz kapcsolódó műveletek
 * kezelésére szolgál.
 *
 * A későbbiekben a weboldalak lekérdezéséhez,
 * létrehozásához és módosításához kapcsolódó üzleti logika elhelyezésére szolgál.
 */
@Service
public class WebsiteService {

    private final WebsiteRepository websiteRepository;

    public WebsiteService(WebsiteRepository websiteRepository) {
        this.websiteRepository = websiteRepository;
    }
}
