package veritas.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import veritas.common.model.ManualCheck;
import veritas.common.model.CrosscheckResult;
import veritas.common.model.Feedback;
import veritas.backend.repository.ManualCheckRepository;
import java.time.LocalDateTime;

@Service
public class ManualCheckService {
    @Autowired
    private ManualCheckRepository manualCheckRepository;
    
    @Autowired
    private CrosscheckService crosscheckService;
    
    public ManualCheck checkQuery(String query) {
        ManualCheck check = new ManualCheck();
        check.setQuery(query);
        check.setCheckedAt(LocalDateTime.now());
        
        CrosscheckResult result = crosscheckService.crosscheckContent(query);
        check.setCrosscheck(result);
        
        return manualCheckRepository.save(check);
    }

    public ManualCheck addFeedback(String id, Feedback feedback) {
        return manualCheckRepository.findById(id)
            .map(check -> {
                check.setFeedback(feedback);
                return manualCheckRepository.save(check);
            })
            .orElseThrow(() -> new RuntimeException("Manual check not found"));
    }
}