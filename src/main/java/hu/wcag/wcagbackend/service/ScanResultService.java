package hu.wcag.wcagbackend.service;

import hu.wcag.wcagbackend.dtos.AiSuggestionDTO;
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
    public List<ScanResult> saveAllResults(List<ScanResult> results) {
        return scanResultRepository.saveAll(results);
    }

    @Transactional(readOnly = true)
    public List<ScanResult> getResultsByScanId(Long scanId) {
        return scanResultRepository.findByScanId(scanId);
    }

    @Transactional
    public ScanResult getById(Long scanResultId){ return scanResultRepository.getById(scanResultId); }

    @Transactional
    public AiSuggestionDTO readSuggestion(ScanResult scanResult){
        if (scanResult.getAiSuggestion() != null)
            return scanResult.getAiSuggestion();
        return null;
    }

    @Transactional
    public void saveSuggestion(Long scanResultId, AiSuggestionDTO suggestion){
        ScanResult scanResult = scanResultRepository.getById(scanResultId);
        scanResult.setAiSuggestion(suggestion);
    }
}
