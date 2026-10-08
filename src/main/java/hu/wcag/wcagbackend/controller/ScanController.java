package hu.wcag.wcagbackend.controller;

import hu.wcag.wcagbackend.dtos.CreateScanRequestDTO;
import hu.wcag.wcagbackend.dtos.ScanDTO;
import hu.wcag.wcagbackend.dtos.ScanResultDTO;
import hu.wcag.wcagbackend.model.Scan;
import hu.wcag.wcagbackend.model.User;
import hu.wcag.wcagbackend.model.Website;
import hu.wcag.wcagbackend.repository.WebsiteRepository;
import hu.wcag.wcagbackend.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/scans")
public class ScanController {

    private final AxeScanService axeScanService;
    private final ScanService scanService;
    private final ScanResultService scanResultService;
    private final UserService userService;
    private final WebsiteRepository websiteRepository;

    public ScanController(AxeScanService axeScanService, ScanService scanService, ScanResultService scanResultService, UserService userService, WebsiteRepository websiteRepository){
        this.axeScanService = axeScanService;
        this.scanService = scanService;
        this.scanResultService = scanResultService;
        this.userService = userService;
        this.websiteRepository = websiteRepository;
    }

    private boolean isValidUrl(String url){
        try {
            URI uri = new URI(url);

            if (uri.getScheme() == null || uri.getHost() == null) {
                return false;
            }

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(3))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .timeout(Duration.ofSeconds(3))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

            return response.statusCode() >= 200 && response.statusCode() < 400;

        } catch (Exception e) {
            return false;
        }
    }

    private String getTitleFromUrl(String url){
        try{
            URI uri = new URI(url);
            String host = uri.getHost();
            return (host != null) ? host : url;
        } catch (Exception e){
            return url;
        }
    }

    @PostMapping
    public ResponseEntity<?> createScan(@RequestBody CreateScanRequestDTO scanRequestDTO) {
        if (!isValidUrl(scanRequestDTO.websiteUrl())){
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "A megadott URL nem létezik vagy jelenleg nem elérhető."));
        }
        User user = userService.getUserById((long) 2); //ToDo jelenlegi user beállítása
        Website website = websiteRepository.findByUrl(scanRequestDTO.websiteUrl())
                .orElseGet(() -> {
                    Website newWebsite = new Website();
                    newWebsite.setUser(user);
                    newWebsite.setUrl(scanRequestDTO.websiteUrl());
                    newWebsite.setTitle(getTitleFromUrl(scanRequestDTO.websiteUrl()));
                    return websiteRepository.save(newWebsite);
                });

            Scan scan = scanService.initiateScan(website.getId());

            return ResponseEntity.ok(ScanDTO.fromModel(scan));
    }

    @PostMapping("/{scanId}/start")
    public ResponseEntity<String> startScan(@PathVariable Long scanId) {
        axeScanService.runScan(scanId);
        return ResponseEntity.ok("A szkennelés sikeresen elindult.");
    }

    @GetMapping("/{scanId}")
    public ResponseEntity<ScanDTO> getScanStatus(@PathVariable Long scanId){
        Scan scan = scanService.getScanById(scanId);
        return ResponseEntity.ok(ScanDTO.fromModel(scan));
    }

    @GetMapping("/{scanId}/results")
    public ResponseEntity<List<ScanResultDTO>> getScanResult(@PathVariable Long scanId){
        List<ScanResultDTO> scanResult = scanResultService.getResultsByScanId(scanId)
                .stream()
                .map(ScanResultDTO::fromModel)
                .toList();
        return ResponseEntity.ok(scanResult);
    }
}
