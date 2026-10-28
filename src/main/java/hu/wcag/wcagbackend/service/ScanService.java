package hu.wcag.wcagbackend.service;

import hu.wcag.wcagbackend.model.Scan;
import hu.wcag.wcagbackend.model.User;
import hu.wcag.wcagbackend.model.Website;
import hu.wcag.wcagbackend.repository.ScanRepository;
import hu.wcag.wcagbackend.repository.UserRepository;
import hu.wcag.wcagbackend.repository.WebsiteRepository;
import hu.wcag.wcagbackend.types.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScanService {

    private final ScanRepository scanRepository;
    private final WebsiteRepository websiteRepository;
    private final UserRepository userRepository;

    public ScanService(ScanRepository scanRepository, WebsiteRepository websiteRepository, UserRepository userRepository) {
        this.scanRepository = scanRepository;
        this.websiteRepository = websiteRepository;
        this.userRepository = userRepository;
    }

    @Transactional
        public Scan initiateScan(Long websiteId, Long userId) {
        Website website = websiteRepository.findById(websiteId)
                .orElseThrow(() -> new RuntimeException("A weboldal nem találahtó ezzel az ID-val:  " + websiteId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("A felhasználó nem található ezzel az ID-val: " + userId));

        Scan scan = new Scan();
        scan.setWebsite(website);
        scan.setUser(user);
        scan.setScannedAt(LocalDateTime.now());
        scan.setStatus(Status.PENDING);

        return scanRepository.save(scan);
    }

    @Transactional
    public void updateScanStatus(Long scanId, Status status) {
        Scan scan = getScanById(scanId);
        scan.setStatus(status);

        if (status == Status.COMPLETED || status == Status.ABORTED) {
            scan.setCompletedAt(LocalDateTime.now());
        }

        scanRepository.save(scan);
    }

    @Transactional(readOnly = true)
    public Scan getScanById(Long id) {
        return scanRepository.findByIdWithWebsite(id)
                .orElseThrow(() -> new RuntimeException("A scan nem található ezzel az ID-val: " + id));
    }

    @Transactional(readOnly = true)
    public List<Scan> getScansByUserId(Long userId) {
        return scanRepository.findByUserId(userId);
    }

}
