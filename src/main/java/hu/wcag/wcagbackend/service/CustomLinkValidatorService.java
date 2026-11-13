package hu.wcag.wcagbackend.service;

import hu.wcag.wcagbackend.config.WcagConfig;
import hu.wcag.wcagbackend.model.Scan;
import hu.wcag.wcagbackend.model.ScanResult;
import hu.wcag.wcagbackend.model.WcagError;
import hu.wcag.wcagbackend.repository.ScanRepository;
import hu.wcag.wcagbackend.repository.ScanResultRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* Weboldalon található túl általános linkszövegek ellenőrzése.
*
* A Selenium WebDriver segítségével összegyűjti az oldal link szövegeit,
* majd a WcagConfig konfigurációban meghatározott szövegek alapján ellenőrzi azok szövegét.
* Egyezés esetén WCAG hibaként kerül mentésre.
*/
@Service
public class CustomLinkValidatorService {

    private final WcagConfig wcagConfig;
    private final ScanRepository scanRepository;
    private final ScanResultRepository scanResultRepository;
    private  final WcagErrorService wcagErrorService;

    public CustomLinkValidatorService(WcagConfig wcagConfig,
                                      ScanRepository scanRepository,
                                      ScanResultRepository scanResultRepository,
                                      WcagErrorService wcagErrorService) {
        this.wcagConfig = wcagConfig;
        this.scanRepository = scanRepository;
        this.scanResultRepository = scanResultRepository;
        this.wcagErrorService = wcagErrorService;
    }

    /*
    * Linkszövegek ellenőrzése
    *
    * Kigyűjti a linkszövegeket, azután a konfigurációban meghatározott szövegeket összehasonlítja az oldalon talált szövegekkel.
    * Egyezés esetén WCAG hibaként mentésre kerül az adott elem.
    */
    public void validateVagueLinks(Long scanId, WebDriver webDriver) {
        /*
        * A túl általános linkszövegek összegyűjtésében a mesterséges intelligencia nyújtott segítéset.
        */
        List<String> vagueLinks = wcagConfig.getVagueLinks();
        List<WebElement> links = webDriver.findElements(By.tagName("a"));


        for (WebElement link : links) {
            link.getText();
            String linkText = link.getText().trim().toLowerCase();

            if (vagueLinks.contains(linkText)) {
                ScanResult scanResult = new ScanResult();

                Scan scan = scanRepository.findById(scanId)
                        .orElseThrow(() -> new RuntimeException("Scan not found with id: " + scanId));

                /*
                 * A hiba adatai mesterséges intelligencia által lettek létrehozva.
                */
                WcagError linkError = wcagErrorService.getOrCreateError(
                        "CUSTOM-VAGUE-LINK",
                        "Vague link text",
                        "The link text is too generic, which causes accessibility issues.",
                        "wcag244"
                );

                scanResult.setScan(scan);
                scanResult.setHtmlElement(link.getAttribute("outerHTML"));
                scanResult.setTargetSelector(generateCssSelector(link));
                scanResult.setErrorLevel("SERIOUS");
                scanResult.setWcagError(linkError);
                scanResult.setAiSuggestion(null);

                scanResultRepository.save(scanResult);
            }
        }
    }

    /*
     * CSS szelekor előállítása a megadott HTML elemhez.
     *
     * Elsődlegesen az elem ID-ja alapján, ennek hiányában az osztályneveket, végül pedig az adott elem típusát adja vissza.
     *
     * Felhasznált forrás: https://stackoverflow.com/questions/30716637/quickly-get-class-attribute-of-every-webelement-with-selenium-in-java
     */
    private String generateCssSelector(WebElement element) {
        String id = element.getAttribute("id");
        if (id != null && !id.isEmpty()) {
            return "#" + id;
        }
        String className = element.getAttribute("class");
        if (className != null && !className.isEmpty()) {
            return "a." + className.trim().replaceAll("\\s+", ".");
        }
        return "a";
    }
}
