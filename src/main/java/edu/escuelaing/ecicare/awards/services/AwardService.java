package edu.escuelaing.ecicare.awards.services;

import java.lang.reflect.Field;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import edu.escuelaing.ecicare.utils.exceptions.notfound.AwardNotFoundException;
import edu.escuelaing.ecicare.awards.models.dto.AwardDto;
import edu.escuelaing.ecicare.awards.models.entity.Award;
import edu.escuelaing.ecicare.services.MapperService;
import edu.escuelaing.ecicare.awards.repositories.AwardRepository;
import lombok.RequiredArgsConstructor;

/**
 * Service layer for handling business logic related to {@link Award} entities.
 *
 * Provides CRUD operations, pagination, search capabilities, and mapping
 * between {@link AwardDto} and {@link Award}.
 */
@Service
@RequiredArgsConstructor
public class AwardService {

    private final AwardRepository awardRepository;
    private final MapperService mapperService;

    /**
     * Retrieves the total number of awards in the repository.
     *
     * @return total count of awards
     */
    public int getAllAwardsLength() {
        return awardRepository.findAll().size();
    }

    /**
     * Retrieves a paginated list of awards based on custom skip/limit logic.
     *
     * @param page the page number (1-based index)
     * @param size the number of awards per page
     * @return a list of awards for the specified page
     * @throws IllegalArgumentException if page or size are less than 1
     */
    public List<Award> getAwardPagination(int page, int size) {
        if (page < 1) {
            throw new IllegalArgumentException("Page number must be 1 or greater");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Page size must be 1 or greater");
        }
        return awardRepository.findAll().stream()
                .skip((long) (page - 1) * size)
                .limit(size)
                .toList();
    }

    /**
     * Retrieves an award by its unique identifier.
     *
     * @param awardId the ID of the award
     * @return the {@link Award} entity
     * @throws AwardNotFoundException if the award does not exist
     */
    public Award getAwardById(Long awardId) {
        return awardRepository.findById(awardId)
                .orElseThrow(() -> new AwardNotFoundException(awardId));
    }

    /**
     * Creates a new award from the provided DTO.
     *
     * If no image URL is provided, a default image path is assigned.
     *
     * @param awardDto the DTO containing award details
     * @return the created {@link Award}
     */
    public Award createAward(AwardDto awardDto) {
        String imageUrl = awardDto.getImageUrl();
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            imageUrl = "/images/awards/default-award.png";
        }

        Award award = Award.builder()
                .name(awardDto.getName())
                .description(awardDto.getDescription())
                .inStock(awardDto.getInStock())
                .imageUrl(imageUrl)
                .build();
        return awardRepository.save(award);
    }

    /**
     * Updates an existing award with new details from the DTO.
     *
     * Fields are updated dynamically using reflection.
     *
     * @param awardId  the ID of the award to update
     * @param awardDto the DTO with updated details
     * @return the updated {@link Award}
     * @throws AwardNotFoundException if the award does not exist
     */
    public Award updateAwardDetails(Long awardId, AwardDto awardDto) {
        Award existingAward = this.getAwardById(awardId);
        mapperService.covertDtoToMap(awardDto)
                .forEach((key, value) -> {
                    if (value != null) {
                        Field field = ReflectionUtils.findField(Award.class, key);
                        if (field != null) {
                            ReflectionUtils.makeAccessible(field);
                            ReflectionUtils.setField(field, existingAward, value);
                        }
                    }
                });
        return awardRepository.save(existingAward);
    }

    /**
     * Deletes an award by its unique identifier.
     *
     * @param awardId the ID of the award to delete
     * @throws AwardNotFoundException if the award does not exist
     */
    public void deleteAwardById(Long awardId) {
        Award award = awardRepository.findById(awardId)
                .orElseThrow(() -> new AwardNotFoundException(awardId));

        awardRepository.delete(award);
    }

    /**
     * Searches awards by name with pagination support.
     *
     * Ensures query validation, trimming, and length constraints.
     *
     * @param searchQuery the text to search for in award names
     * @param page        the page number (0-based index)
     * @param size        the number of results per page
     * @return a {@link Page} of awards matching the search criteria
     */
    public Page<Award> searchAwardsByNamePaginated(String searchQuery, int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > 100) {
            size = 8;
        }
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return Page.empty(PageRequest.of(page, size));
        }
        if (searchQuery.trim().length() > 50) {
            searchQuery = searchQuery.trim().substring(0, 50);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return awardRepository.findByNameContainingIgnoreCase(searchQuery.trim(), pageable);
    }
}
