package hu.wcag.wcagbackend.service;

import hu.wcag.wcagbackend.dtos.AiSuggestionDTO;
import hu.wcag.wcagbackend.model.ScanResult;
import hu.wcag.wcagbackend.repository.ScanResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
* WCAG vizsgálatok során létrejött eredmények kezelése.
*
* Felelős a viszgálat eredményeinek mentéséért, lekéréséért,
* valamint az eredményekhez tartozó AI alapú javítási javaslatok tárolásáért és lekéréséért.
*/
@Service
public class ScanResultService {

    private final ScanResultRepository scanResultRepository;

    public ScanResultService(ScanResultRepository scanResultRepository) {
        this.scanResultRepository = scanResultRepository;
    }

    /*
     * A megadott vizsgálati eredmények együttes mentése az adatbázisba.
     */
    @Transactional
    public List<ScanResult> saveAllResults(List<ScanResult> results) {
        return scanResultRepository.saveAll(results);
    }

    /*
     * A megadott WCAG vizsgálathoz tartozó eredmények lekérdezése.
     */
    @Transactional(readOnly = true)
    public List<ScanResult> getResultsByScanId(Long scanId) {
        return scanResultRepository.findByScanId(scanId);
    }

    /*
     * Vizsgálati eredmény lekérése azonosító alapján. Ez az AI javaslat elkészítéséhez szükséges.
     */
    @Transactional
    public ScanResult getById(Long scanResultId){ return scanResultRepository.getById(scanResultId); }

    /*
     * A vizsgálati eredményhez korábban elmentett AI javaslat lekérése.
     * Ha nem tartozik hozzá javaslat, null értéket ad vissza.
     */
    @Transactional
    public AiSuggestionDTO readSuggestion(ScanResult scanResult){
        if (scanResult.getAiSuggestion() != null)
            return scanResult.getAiSuggestion();
        return null;
    }

    /*
     * Az MI által generált javítási javaslat hozzárendelése a megadott vizsgálati eredményhez.
     */
    @Transactional
    public void saveSuggestion(Long scanResultId, AiSuggestionDTO suggestion){
        ScanResult scanResult = scanResultRepository.getById(scanResultId);
        scanResult.setAiSuggestion(suggestion);
    }
}
