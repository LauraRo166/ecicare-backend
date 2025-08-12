package edu.escuelaing.ecicare.premios.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.escuelaing.ecicare.premios.models.dto.RedeemableDto;
import edu.escuelaing.ecicare.premios.models.entity.Redeemable;
import edu.escuelaing.ecicare.premios.services.RedeemableService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redeemables")
public class RedeemableController {

    private final RedeemableService redeemableService;

    @GetMapping
    public ResponseEntity<List<Redeemable>> getAllRedeemables() {
        return ResponseEntity.ok(redeemableService.getAllRedeemables());
    }

    @GetMapping("/{challengeId}/{awardId}")
    public ResponseEntity<Redeemable> getRedeemableById(
            @PathVariable Long challengeId,
            @PathVariable Long awardId) {
        return ResponseEntity.ok(redeemableService.getRedeemableById(challengeId, awardId));
    }

    @PutMapping("/{challengeId}/{awardId}")
    public ResponseEntity<Redeemable> updateRedeemable(
            @PathVariable Long challengeId,
            @PathVariable Long awardId,
            @RequestBody RedeemableDto redeemableDto) {
        return ResponseEntity.ok(redeemableService.updateRedeemable(challengeId, awardId, redeemableDto));
    }

    @DeleteMapping("/{challengeId}/{awardId}")
    public ResponseEntity<Void> deleteRedeemable(
            @PathVariable Long challengeId,
            @PathVariable Long awardId) {
        redeemableService.deleteRedeemable(challengeId, awardId);
        return ResponseEntity.noContent().build();
    }
}
