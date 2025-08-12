package edu.escuelaing.ecicare.premios.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.escuelaing.ecicare.premios.models.dto.AwardDto;
import edu.escuelaing.ecicare.premios.models.entity.Award;
import edu.escuelaing.ecicare.premios.services.AwardService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/awards")
public class AwardController {

    private final AwardService awardService;

    @PostMapping
    public ResponseEntity<Award> createAward(@RequestBody AwardDto awardDto) {
        return ResponseEntity.ok(awardService.createAward(awardDto));
    }

    @GetMapping
    public ResponseEntity<List<Award>> getAllAwards() {
        return ResponseEntity.ok(awardService.getAllAwards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Award> getAwardById(@PathVariable Long id) {
        return ResponseEntity.ok(awardService.getAwardById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Award> updateAward(@PathVariable Long id, @RequestBody AwardDto awardDto) {
        return ResponseEntity.ok(awardService.updateAwardDetails(id, awardDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAwardById(@PathVariable Long id) {
        awardService.deleteAwardById(id);
        return ResponseEntity.noContent().build();
    }

}