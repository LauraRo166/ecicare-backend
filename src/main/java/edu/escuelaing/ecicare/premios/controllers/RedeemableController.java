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

    @GetMapping("/{challengeName}/{awardId}")
    public ResponseEntity<Redeemable> getRedeemableById(
            @PathVariable String challengeName,
            @PathVariable Long awardId) {
        return ResponseEntity.ok(redeemableService.getRedeemableById(challengeName, awardId));
    }

    @PostMapping
    public ResponseEntity<Redeemable> createRedeemable(@RequestBody RedeemableDto redeemableDto) {
        Redeemable created = redeemableService.createRedeemableToChallenge(redeemableDto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{challengeName}/{awardId}")
    public ResponseEntity<Redeemable> updateRedeemable(
            @PathVariable String challengeName,
            @PathVariable Long awardId,
            @RequestBody RedeemableDto redeemableDto) {
        return ResponseEntity.ok(redeemableService.updateRedeemable(challengeName, awardId, redeemableDto));
    }

    @DeleteMapping("/{challengeName}/{awardId}")
    public ResponseEntity<Void> deleteRedeemable(
            @PathVariable String challengeName,
            @PathVariable Long awardId) {
        redeemableService.deleteRedeemable(challengeName, awardId);
        return ResponseEntity.noContent().build();
    }
}
