package edu.escuelaing.ecicare.premios.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
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

    @GetMapping("/total")
    public ResponseEntity<Map<String, Integer>> getAllAwardsLength() {
        int total = awardService.getAllAwardsLength();
        return ResponseEntity.ok(Collections.singletonMap("total", total));
    }

    @GetMapping
    public ResponseEntity<List<Award>> getAwardPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(awardService.getAwardPagination(page, size));
    }

    /**
     * Search awards by name - Perfect for real-time search functionality.
     * Only performs search when user actually types something.
     * When search is empty, returns empty result to maintain existing pagination.
     * 
     * @param q    the search query (what the user is typing)
     * @param page the page number (optional, defaults to 0)
     * @param size the page size (optional, defaults to 8 for UI grid)
     * @return ResponseEntity containing search results only when there's actual
     *         search text
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

    @GetMapping("/{id}")
    public ResponseEntity<Award> getAwardById(@PathVariable Long id) {
        return ResponseEntity.ok(awardService.getAwardById(id));
    }

    @PostMapping
    public ResponseEntity<Award> createAward(@RequestBody AwardDto awardDto) {
        return ResponseEntity.ok(awardService.createAward(awardDto));
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