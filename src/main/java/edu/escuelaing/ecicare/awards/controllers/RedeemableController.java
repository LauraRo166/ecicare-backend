package edu.escuelaing.ecicare.awards.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.escuelaing.ecicare.awards.models.dto.RedeemableDto;
import edu.escuelaing.ecicare.awards.models.dto.RedeemableResponse;
import edu.escuelaing.ecicare.awards.models.entity.Redeemable;
import edu.escuelaing.ecicare.awards.models.mapper.RedeemableMapper;
import edu.escuelaing.ecicare.awards.services.RedeemableService;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller that manages operations related to {@link Redeemable}
 * entities
 * in the Ecicare system.
 *
 * This controller delegates business logic to {@link RedeemableService}.
 *
 * @author Byte programming
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/redeemables")
public class RedeemableController {

    private final RedeemableService redeemableService;

    /**
     * Retrieves all redeemables in the system.
     *
     * @return a {@link ResponseEntity} containing a list of {@link Redeemable}.
     */
    @GetMapping
    public ResponseEntity<List<RedeemableResponse>> getAllRedeemables() {
        return ResponseEntity.ok(redeemableService.getAllRedeemables().stream()
                .map(RedeemableMapper::toResponse)
                .toList());
    }

    /**
     * Retrieves a redeemable by its composite identifier
     * consisting of {@code challengeName} and {@code awardId}.
     *
     * @param challengeName the challenge name associated with the redeemable.
     * @param awardId       the award identifier associated with the redeemable.
     * @return a {@link ResponseEntity} containing the {@link Redeemable}.
     */
    @GetMapping("/{challengeName}/{awardId}")
    public ResponseEntity<RedeemableResponse> getRedeemableById(
            @PathVariable String challengeName,
            @PathVariable Long awardId) {
        return ResponseEntity.ok(RedeemableMapper.toResponse(redeemableService.getRedeemableById(challengeName, awardId)));
    }

    /**
     * Creates a new redeemable linked to a challenge.
     *
     * @param redeemableDto the DTO containing redeemable creation data.
     * @return a {@link ResponseEntity} containing the created {@link Redeemable}.
     */
    @PostMapping
    public ResponseEntity<RedeemableResponse> createRedeemable(@RequestBody RedeemableDto redeemableDto) {
        Redeemable created = redeemableService.createRedeemableToChallenge(redeemableDto);
        return ResponseEntity.ok(RedeemableMapper.toResponse(created));
    }

    /**
     * Updates an existing redeemable identified by {@code challengeName} and
     * {@code awardId}.
     *
     * @param challengeName the challenge name of the redeemable.
     * @param awardId       the award identifier of the redeemable.
     * @param redeemableDto the DTO containing updated redeemable data.
     * @return a {@link ResponseEntity} containing the updated {@link Redeemable}.
     */
    @PutMapping("/{challengeName}/{awardId}")
    public ResponseEntity<RedeemableResponse> updateRedeemable(
            @PathVariable String challengeName,
            @PathVariable Long awardId,
            @RequestBody RedeemableDto redeemableDto) {
        return ResponseEntity.ok(RedeemableMapper.toResponse(redeemableService.updateRedeemable(challengeName, awardId, redeemableDto)));
    }

    /**
     * Deletes a redeemable by its composite identifier.
     *
     * @param challengeName the challenge name of the redeemable.
     * @param awardId       the award identifier of the redeemable.
     * @return a {@link ResponseEntity} with HTTP status {@code 204 No Content}.
     */
    @DeleteMapping("/{challengeName}/{awardId}")
    public ResponseEntity<Void> deleteRedeemable(
            @PathVariable String challengeName,
            @PathVariable Long awardId) {
        redeemableService.deleteRedeemable(challengeName, awardId);
        return ResponseEntity.noContent().build();
    }
}
