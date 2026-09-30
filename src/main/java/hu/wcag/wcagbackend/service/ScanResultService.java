package hu.wcag.wcagbackend.service;

import hu.wcag.wcagbackend.model.ScanResult;
import hu.wcag.wcagbackend.repository.ScanResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScanResultService {

    private final ScanResultRepository scanResultRepository;

    public ScanResultService(ScanResultRepository scanResultRepository) {
        this.scanResultRepository = scanResultRepository;
    }

    @Transactional
    public ScanResult saveResult(ScanResult scanResult) {
        return scanResultRepository.save(scanResult);
    }

    @Transactional
    public List<ScanResult> saveAllResults(List<ScanResult> results) {
        return scanResultRepository.saveAll(results);
    }

    @Transactional(readOnly = true)
    public List<ScanResult> getResultsByScanId(Long scanId) {
        return scanResultRepository.findByScanId(scanId);
    }

    @Transactional(readOnly = true)
    public List<ScanResult> getResultsByScanIdAndErrorId(Long scanId, Long errorId) {
        return scanResultRepository.findByScanIdAndWcagErrorId(scanId, errorId);
    }
}
