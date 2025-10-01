package edu.escuelaing.ecicare.awards.services;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
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

@Service
@RequiredArgsConstructor
public class AwardService {

    private final AwardRepository awardRepository;
    private final MapperService mapperService;

    public int getAllAwardsLength() {
        return awardRepository.findAll().size();
    }

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

    public Award getAwardById(Long awardId) {
        Optional<Award> award = awardRepository.findById(awardId);
        if (!award.isPresent())
            throw new AwardNotFoundException(awardId);
        return award.get();
    }

    public Award createAward(AwardDto awardDto) {
        String imageUrl = awardDto.getImageUrl();
        // Si no se proporciona una URL de imagen, usar la imagen por defecto
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

    public Award updateAwardDetails(Long awardId, AwardDto awardDto) {
        Award existingAward = this.getAwardById(awardId);
        mapperService.covertDtoToMap(awardDto)
                .forEach((key, value) -> {
                    Field field = ReflectionUtils.findField(Award.class, key);
                    if (field != null) {
                        ReflectionUtils.makeAccessible(field);
                        ReflectionUtils.setField(field, existingAward, value);
                    }
                });
        return awardRepository.save(existingAward);
    }

    public void deleteAwardById(Long awardId) {
        awardRepository.deleteById(awardId);
    }

    public void saveAward(Award award) {
        awardRepository.save(award);
    }

    /**
     * Searches awards by name with pagination support.
     * Optimized for real-time search with large datasets.
     * 
     * @param searchQuery the text to search for in award names
     * @param page        the page number (0-based)
     * @param size        the page size
     * @return page of awards matching the search criteria
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