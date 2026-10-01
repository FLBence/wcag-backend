package hu.wcag.wcagbackend.service;

import com.deque.html.axecore.results.CheckedNode;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;
import hu.wcag.wcagbackend.model.Scan;
import hu.wcag.wcagbackend.model.ScanResult;
import hu.wcag.wcagbackend.model.WcagError;
import hu.wcag.wcagbackend.types.Status;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AxeScanService {

    private static final Logger logger = LoggerFactory.getLogger(AxeScanService.class);

    private final ScanService scanService;
    private final ScanResultService scanResultService;
    private final WcagErrorService wcagErrorService;

    public AxeScanService(ScanService scanService, ScanResultService scanResultService, WcagErrorService wcagErrorService){
        this.scanService = scanService;
        this.scanResultService = scanResultService;
        this.wcagErrorService = wcagErrorService;
    }

    @Async
    public void runScan(Long scanId){
        Scan scan = scanService.getScanById(scanId);
        String websiteURL = scan.getWebsite().getUrl();

        scanService.updateScanStatus(scanId, Status.IN_PROGRESS);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        WebDriver driver = null;

        try{
            driver = new ChromeDriver(options);
            driver.get(websiteURL);

            AxeBuilder builder = new AxeBuilder();
            Results results = builder.analyze(driver);

            List<ScanResult> scanResultsToSave = new ArrayList<>();
            for (Rule violation : results.getViolations()) {
                String wcagTags = "";
                if (violation.getTags() != null) {
                    wcagTags = violation.getTags().stream()
                            .filter(tag -> tag.startsWith("wcag"))
                            .collect(Collectors.joining(", "));
                }

                WcagError wcagError = wcagErrorService.getOrCreateError(
                        violation.getId(),
                        violation.getHelp() != null ? violation.getHelp() : violation.getId(),
                        violation.getDescription(),
                        wcagTags
                );

                if (violation.getNodes() != null) {
                    for (CheckedNode node : violation.getNodes()) {
                        String htmlSnippet = node.getHtml() != null ? node.getHtml() : "";
                        String targetSelector = node.getTarget() != null ? node.getTarget().toString() : "";

                        ScanResult scanResult = new ScanResult();
                        scanResult.setScan(scan);
                        scanResult.setWcagError(wcagError);
                        scanResult.setTargetSelector(targetSelector);
                        scanResult.setHtmlElement(htmlSnippet);
                        if (violation.getImpact() != null) {
                            scanResult.setErrorLevel(violation.getImpact().toUpperCase());
                        } else {
                            scanResult.setErrorLevel("UNKNOWN");
                        }

                        scanResultsToSave.add(scanResult);
                    }
                }
            }

            scanResultService.saveAllResults(scanResultsToSave);
            scanService.updateScanStatus(scanId, Status.COMPLETED);
            logger.info("A szkennelés sikeresen befejeződött (scanId: {})", scanId);
        }catch (Exception e){
            logger.error("Hiba történt a szkennelés során (scanId: {}): {}", scanId, e.getMessage(), e);
            scanService.updateScanStatus(scanId, Status.ABORTED);
        }finally {
            if (driver != null){
                driver.quit();
            }
        }

    }
}
