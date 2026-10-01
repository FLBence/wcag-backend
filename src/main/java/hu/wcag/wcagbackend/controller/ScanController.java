package hu.wcag.wcagbackend.controller;

import hu.wcag.wcagbackend.model.Scan;
import hu.wcag.wcagbackend.service.AxeScanService;
import hu.wcag.wcagbackend.service.ScanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scans")
public class ScanController {

    private final AxeScanService axeScanService;
    private final ScanService scanService;

    public ScanController(AxeScanService axeScanService, ScanService scanService){
        this.axeScanService = axeScanService;
        this.scanService = scanService;
    }

    @PostMapping("/{scanId}/start")
    public ResponseEntity<String> startScan(@PathVariable Long scanId) {
        axeScanService.runScan(scanId);
        return ResponseEntity.ok("A szkennelés sikeresen elindult. ScanID:" + scanId);
    }

    @GetMapping("/{scanId}")
    public ResponseEntity<Scan> getScanStatus(@PathVariable Long scanId){
        Scan scan = scanService.getScanById(scanId);
        return ResponseEntity.ok(scan);
    }
}
