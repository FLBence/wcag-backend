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

import java.util.List;
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

    public ScanController(AxeScanService axeScanService, ScanService scanService, ScanResultService scanResultService, UserService userService, WebsiteRepository websiteRepository){
        this.axeScanService = axeScanService;
        this.scanService = scanService;
        this.scanResultService = scanResultService;
        this.userService = userService;
        this.websiteRepository = websiteRepository;
    }

    @PostMapping
    public ResponseEntity<ScanDTO> createScan(@RequestBody CreateScanRequestDTO scanRequestDTO){
        User user = userService.getUserById((long) 2); //ToDo jelenlegi user beállítása
        Website website = websiteRepository.findByUrl(scanRequestDTO.websiteUrl())
                .orElseGet(() -> {
                    Website newWebsite = new Website();
                    String title = scanRequestDTO.websiteUrl();
                    title = title.split("//")[1];
                    title = title.split("\\.")[1];
                    newWebsite.setUser(user);
                    newWebsite.setUrl(scanRequestDTO.websiteUrl());
                    newWebsite.setTitle(title);
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
