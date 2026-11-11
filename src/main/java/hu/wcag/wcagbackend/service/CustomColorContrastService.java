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

    public void validateColorContrast(Long scanId, WebDriver driver){
        Scan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new RuntimeException("Nem található scan ezzel az ID-vel: " + scanId));

        List<WebElement> elements = driver.findElements(By.cssSelector("a, button, p, span, h1, h2, h3, h4, h5, h6, label, li"));
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

                if (contrastRatio < 4.5) {
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

}
