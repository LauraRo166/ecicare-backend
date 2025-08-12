package edu.escuelaing.ecicare.premios.services;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import edu.escuelaing.ecicare.exceptions.notfound.AwardNotFoundException;
import edu.escuelaing.ecicare.premios.models.dto.AwardDto;
import edu.escuelaing.ecicare.premios.models.entity.Award;
import edu.escuelaing.ecicare.services.MapperService;
import edu.escuelaing.ecicare.premios.repositories.AwardRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AwardService {

    private final AwardRepository awardRepository;
    private final MapperService mapperService;

    public List<Award> getAllAwards() {
        return awardRepository.findAll();
    }

    public Award getAwardById(Long awardId) {
        Optional<Award> award = awardRepository.findById(awardId);
        if (!award.isPresent()) throw new AwardNotFoundException(awardId);
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
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .createdBy(awardDto.getUpdatedBy())
                .updatedBy(awardDto.getUpdatedBy())
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
        existingAward.setUpdateDate(LocalDateTime.now());
        existingAward.setUpdatedBy(awardDto.getUpdatedBy());
        return awardRepository.save(existingAward);
    }

    public void deleteAwardById(Long awardId) {
        awardRepository.deleteById(awardId);
    }

    public void saveAward(Award award) {
        awardRepository.save(award);
    }

}