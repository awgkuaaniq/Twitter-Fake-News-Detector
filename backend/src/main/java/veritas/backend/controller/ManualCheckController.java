package veritas.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import veritas.common.model.ManualCheck;
import veritas.backend.service.ManualCheckService;

@RestController
@RequestMapping("/api/manual-checks")
@CrossOrigin(origins = "*")
public class ManualCheckController {
    @Autowired
    private ManualCheckService manualCheckService;
    
    @PostMapping
    public ResponseEntity<ManualCheck> checkQuery(@RequestParam String query) {
        return ResponseEntity.ok(manualCheckService.checkQuery(query));
    }
}