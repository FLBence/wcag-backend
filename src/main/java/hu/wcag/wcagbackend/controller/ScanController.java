package hu.wcag.wcagbackend.controller;

import hu.wcag.wcagbackend.dtos.AiSuggestionDTO;
import hu.wcag.wcagbackend.dtos.CreateScanRequestDTO;
import hu.wcag.wcagbackend.dtos.ScanDTO;
import hu.wcag.wcagbackend.dtos.ScanResultDTO;
import hu.wcag.wcagbackend.model.Scan;
import hu.wcag.wcagbackend.model.User;
import hu.wcag.wcagbackend.model.Website;
import hu.wcag.wcagbackend.repository.ScanRepository;
import hu.wcag.wcagbackend.repository.ScanResultRepository;
import hu.wcag.wcagbackend.repository.WebsiteRepository;
import hu.wcag.wcagbackend.service.*;
import hu.wcag.wcagbackend.types.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/scans")
public class ScanController {

    private final AxeScanService axeScanService;
    private final ScanService scanService;
    private final ScanResultService scanResultService;
    private final UserService userService;
    private final WebsiteRepository websiteRepository;
    private final ScanRepository scanRepository;
    private final ScanResultRepository scanResultRepository;
    private final AiSuggestionService aiSuggestionService;

    public ScanController(AxeScanService axeScanService,
                          ScanService scanService,
                          ScanResultService scanResultService,
                          UserService userService,
                          WebsiteRepository websiteRepository,
                          ScanRepository scanRepository,
                          ScanResultRepository scanResultRepository,
                          AiSuggestionService aiSuggestionService){
        this.axeScanService = axeScanService;
        this.scanService = scanService;
        this.scanResultService = scanResultService;
        this.userService = userService;
        this.websiteRepository = websiteRepository;
        this.scanRepository = scanRepository;
        this.scanResultRepository = scanResultRepository;
        this.aiSuggestionService = aiSuggestionService;
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

    @Transactional
    @PostMapping
    public ResponseEntity<?> createScan(@RequestBody CreateScanRequestDTO scanRequestDTO) {
        if (!isValidUrl(scanRequestDTO.websiteUrl())){
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "A megadott URL nem létezik vagy jelenleg nem elérhető."));
        }
        User user = userService.getUserById(scanRequestDTO.id());
        Website website = websiteRepository.findByUrl(scanRequestDTO.websiteUrl())
                .orElseGet(() -> {
                    Website newWebsite = new Website();
                    newWebsite.setUrl(scanRequestDTO.websiteUrl());
                    newWebsite.setTitle(getTitleFromUrl(scanRequestDTO.websiteUrl()));
                    return websiteRepository.save(newWebsite);
                });

        Optional<Scan> existingScan = scanRepository.findByUserIdAndWebsiteId(user.getId(), website.getId());
        Scan scan;
        if (existingScan.isPresent()) {
            scan = existingScan.get();
            scan.setStatus(Status.PENDING);
            scan.setScannedAt(LocalDateTime.now());
            scanResultRepository.deleteByScanId(scan.getId());
            scan = scanRepository.save(scan);
            scanService.updateScanStatus(scan.getId(), Status.COMPLETED);
        } else
            scan = scanService.initiateScan(website.getId(),user.getId());

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

    @GetMapping("/{scanId}/results/{scanResultId}/suggestion")
    public ResponseEntity<AiSuggestionDTO> getSuggestion(@PathVariable("scanId") Long scanId, @PathVariable("scanResultId") Long scanResultId){
        AiSuggestionDTO suggestion = aiSuggestionService.getSuggestion(scanId, scanResultId);
        if (suggestion == null)
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        return ResponseEntity.ok(suggestion);
    }
}
