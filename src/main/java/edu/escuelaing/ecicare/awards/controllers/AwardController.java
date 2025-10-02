package edu.escuelaing.ecicare.awards.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.escuelaing.ecicare.awards.models.dto.AwardDto;
import edu.escuelaing.ecicare.awards.models.entity.Award;
import edu.escuelaing.ecicare.awards.services.AwardService;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller that manages operations related to {@link Award} entities
 * in the Ecicare system.
 *
 * This class delegates business logic to {@link AwardService}.
 *
 * @author Byte programming
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/awards")
public class AwardController {

    private final AwardService awardService;

    /**
     * Retrieves the total number of awards in the system.
     *
     * @return a {@link ResponseEntity} containing a {@link Map} with the key
     *         "total" and the total number of awards.
     */
    @GetMapping("/total")
    public ResponseEntity<Map<String, Integer>> getAllAwardsLength() {
        int total = awardService.getAllAwardsLength();
        return ResponseEntity.ok(Collections.singletonMap("total", total));
    }

    /**
     * Retrieves a paginated list of awards.
     *
     * @param page the page number (defaults to 1).
     * @param size the number of elements per page (defaults to 10).
     * @return a {@link ResponseEntity} containing a list of {@link Award}.
     */
    @GetMapping
    public ResponseEntity<List<Award>> getAwardPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(awardService.getAwardPagination(page, size));
    }

    /**
     * Searches for awards by name. Designed for real-time search functionality.
     *
     * @param q    the search query (partial or full award name).
     * @param page the page number (defaults to 0).
     * @param size the page size (defaults to 8).
     * @return a {@link ResponseEntity} containing a {@link Page} of {@link Award}
     *         when results exist, or an empty list when {@code q} is empty.
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchAwards(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        Page<Award> searchResults = awardService.searchAwardsByNamePaginated(q, page, size);

        return ResponseEntity.ok(searchResults);
    }

    /**
     * Retrieves an award by its unique identifier.
     *
     * @param id the award identifier.
     * @return a {@link ResponseEntity} containing the {@link Award}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Award> getAwardById(@PathVariable Long id) {
        return ResponseEntity.ok(awardService.getAwardById(id));
    }

    /**
     * Creates a new award.
     *
     * @param awardDto the DTO containing award creation data.
     * @return a {@link ResponseEntity} containing the created {@link Award}.
     */
    @PostMapping
    public ResponseEntity<Award> createAward(@RequestBody AwardDto awardDto) {
        return ResponseEntity.ok(awardService.createAward(awardDto));
    }

    /**
     * Updates the details of an existing award.
     *
     * @param id       the identifier of the award to update.
     * @param awardDto the DTO containing updated award data.
     * @return a {@link ResponseEntity} containing the updated {@link Award}.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Award> updateAward(@PathVariable Long id, @RequestBody AwardDto awardDto) {
        return ResponseEntity.ok(awardService.updateAwardDetails(id, awardDto));
    }

    /**
     * Deletes an award by its unique identifier.
     *
     * @param id the identifier of the award to delete.
     * @return a {@link ResponseEntity} with HTTP status {@code 204 No Content}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAwardById(@PathVariable Long id) {
        awardService.deleteAwardById(id);
        return ResponseEntity.noContent().build();
    }

}
