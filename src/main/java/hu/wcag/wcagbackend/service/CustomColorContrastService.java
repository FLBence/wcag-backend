package hu.wcag.wcagbackend.service;

import hu.wcag.wcagbackend.model.Scan;
import hu.wcag.wcagbackend.model.ScanResult;
import hu.wcag.wcagbackend.model.WcagError;
import hu.wcag.wcagbackend.repository.ScanRepository;
import hu.wcag.wcagbackend.repository.ScanResultRepository;
import hu.wcag.wcagbackend.util.ColorUtil;
import hu.wcag.wcagbackend.util.ContrastCalculator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* A megadott weboldal szöveges elemeinek egyedi színkontraszt vizsgálata.
*
* A Selenium WebDriver segítségével lekéri az oldalon található szöveges elemek szöveg továbbá háttérszínét,
* és a ContrastCalculator segédosztály segítségével kiszámítja a kontrasztarányt,
* amennyiben az arány nem éri el a WCAG szabvány által megszabott minimumot azonosítja az elemeket és WCAG hibaként rögzíti.
*
* A kódrészlet mesterséges intelligencia segítségével készült.
*/
@Service
public class CustomColorContrastService {

    private static final Logger logger = LoggerFactory.getLogger(CustomColorContrastService.class);

    private final ScanRepository scanRepository;
    private final ScanResultRepository scanResultRepository;
    private  final WcagErrorService wcagErrorService;

    public CustomColorContrastService(ScanRepository scanRepository,
                                      ScanResultRepository scanResultRepository,
                                      WcagErrorService wcagErrorService){
        this.scanRepository = scanRepository;
        this.scanResultRepository = scanResultRepository;
        this.wcagErrorService = wcagErrorService;
    }

    /*
    * Kontrasztvalidálást elvégzéséért felelős függvény.
    *
    * Kigyűjti a szöveget tartalmazó elemeket és végigellenőrzi, hogy megfelel-e a WCAG által meghatározott kontrasztértékeknek,
    * amennyiben nem új hibaként elmenti.
    * */
    public void validateColorContrast(Long scanId, WebDriver driver){
        Scan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new RuntimeException("Nem található scan ezzel az ID-vel: " + scanId));

        List<WebElement> elements = driver.findElements(By.cssSelector("a, button, p, span, h1, h2, h3, h4, h5, h6, label, li, input"));
        Actions actions = new Actions(driver);

        for (WebElement element : elements){
            try {
                String tagname = element.getTagName().toLowerCase();
                boolean isInteractive = tagname.equals("a") || tagname.equals("button") || tagname.equals("input");

                if (element.getText().isBlank()) {
                    continue;
                }

                if (isInteractive) {
                    try {
                        actions.moveToElement(element).perform();
                    }catch (Exception ignored){
                        continue;
                    }
                }
                String textColor = element.getCssValue("color");
                String bgColor = element.getCssValue("background-color");

                double contrastRatio = ContrastCalculator.calculateContrastRatio(ColorUtil.parseColor(textColor), ColorUtil.parseColor(bgColor));

                double req = isLargeText(element) ? 3.0 : 4.5;
                if (contrastRatio < req) {
                    /*
                    * A hiba adatai mesterséges intelligencia által lettek létrehozva.
                    */
                    WcagError contrastError = wcagErrorService.getOrCreateError(
                            "CUSTOM-COLOR-CONTRAST",
                            "Insufficient color contrast",
                            "The contrast ratio of text and background does not reach the minimum required by WCAG AA.",
                            "wcag143"
                    );
                    ScanResult result = new ScanResult();
                    result.setScan(scan);
                    result.setHtmlElement(element.getAttribute("outerHTML"));
                    result.setWcagError(contrastError);
                    result.setTargetSelector(generateCssSelector(element));
                    result.setErrorLevel("SERIOUS");
                    result.setAiSuggestion(null);

                    scanResultRepository.save(result);
                }
            }catch (Exception e){
                logger.warn("Hiba történt egyedi elem színkontraszt vizsgálata közben: {}", e.getMessage());
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
    private String generateCssSelector(WebElement element){
        String id = element.getAttribute("id");
        if (id != null && !id.isEmpty())
            return "#" + id;
        String className = element.getAttribute("class");
        if (className != null && !className.isEmpty()) {
            return element.getTagName() + "." + className.trim().replaceAll("\\s+", ".");
        }
        return element.getTagName();
    }

    /*
    * Szöveg méret ellenőrző
    *
    * A WCAG szabvány eltérő kontrasztarányokat határozott meg a normál és a nagy méretű szövegek esetében.
    * Megvizsgálja az adott elem szövegének méretét és betűvastagságát (dőlt), ezek alapján eldönti hogy a WCAG szabvány alapján normál vagy nagy méretű-e a szöveg.
    */
    private boolean isLargeText(WebElement element) {
        try {
            String fontSizePx = element.getCssValue("font-size");
            int boldNum = Integer.parseInt(element.getCssValue("font-weight"));
            boolean bold = boldNum >= 700;
            double fontSize = Double.parseDouble(fontSizePx.replace("px", "").trim());
            return fontSize >= 24 || (bold && fontSize >= 18.66);
        }catch (Exception e){
            return false;
        }
    }

}
