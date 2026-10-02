package hu.wcag.wcagbackend.controller;

import hu.wcag.wcagbackend.dtos.ScanDTO;
import hu.wcag.wcagbackend.dtos.ScanResultDTO;
import hu.wcag.wcagbackend.model.Scan;
import hu.wcag.wcagbackend.model.ScanResult;
import hu.wcag.wcagbackend.service.AxeScanService;
import hu.wcag.wcagbackend.service.ScanResultService;
import hu.wcag.wcagbackend.service.ScanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/scans")
public class ScanController {

    private final AxeScanService axeScanService;
    private final ScanService scanService;
    private final ScanResultService scanResultService;

    public ScanController(AxeScanService axeScanService, ScanService scanService, ScanResultService scanResultService){
        this.axeScanService = axeScanService;
        this.scanService = scanService;
        this.scanResultService = scanResultService;
    }

    @PostMapping("/{scanId}/start")
    public ResponseEntity<String> startScan(@PathVariable Long scanId) {
        axeScanService.runScan(scanId);
        return ResponseEntity.ok("A szkennelés sikeresen elindult. ScanID:" + scanId);
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
