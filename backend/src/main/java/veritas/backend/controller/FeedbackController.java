package veritas.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import veritas.common.model.ManualCheck;
import veritas.common.model.Feedback;
import veritas.backend.service.ManualCheckService;

@RestController
@RequestMapping("/api/manual-checks")
@CrossOrigin(origins = "*")
public class FeedbackController {
    @Autowired
    private ManualCheckService manualCheckService;
    
    @PostMapping("/{id}/feedback")
    public ResponseEntity<ManualCheck> addFeedback(
            @PathVariable String id,
            @RequestBody Feedback feedback) {
        return ResponseEntity.ok(manualCheckService.addFeedback(id, feedback));
    }
}