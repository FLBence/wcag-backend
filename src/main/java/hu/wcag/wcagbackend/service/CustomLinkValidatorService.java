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

    public void validateVagueLinks(Long scanId, WebDriver webDriver) {
        List<String> vagueLinks = wcagConfig.getVagueLinks();
        List<WebElement> links = webDriver.findElements(By.tagName("a"));


        for (WebElement link : links) {
            link.getText();
            String linkText = link.getText().trim().toLowerCase();

            if (vagueLinks.contains(linkText)) {
                ScanResult scanResult = new ScanResult();

                Scan scan = scanRepository.findById(scanId)
                        .orElseThrow(() -> new RuntimeException("Scan not found with id: " + scanId));

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
                scanResult.setAiSuggestion("");

                scanResultRepository.save(scanResult);
            }
        }
    }

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
