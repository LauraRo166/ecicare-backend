package edu.escuelaing.ecicare.premios.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import edu.escuelaing.ecicare.utils.exceptions.notfound.RedeemableNotFoundException;
import edu.escuelaing.ecicare.premios.models.dto.RedeemableDto;
import edu.escuelaing.ecicare.premios.models.entity.Award;
import edu.escuelaing.ecicare.premios.models.entity.Redeemable;
import edu.escuelaing.ecicare.premios.models.entity.RedeemableId;
import edu.escuelaing.ecicare.premios.repositories.RedeemableRepository;
import edu.escuelaing.ecicare.retos.models.entity.Challenge;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedeemableService {

    private final RedeemableRepository redeemableRepository;
    private final AwardService awardService;

    public List<Redeemable> getAllRedeemables() {
        return redeemableRepository.findAll();
    }

    public Redeemable getRedeemableById(String challengeName, Long awardId) {
        Optional<Redeemable> redeemable = redeemableRepository.findById(new RedeemableId(challengeName, awardId));
        if (!redeemable.isPresent())
            throw new RedeemableNotFoundException(challengeName, awardId);
        return redeemable.get();
    }

    public List<Redeemable> createRedeemablesToChallenge(List<RedeemableDto> redeemables, Challenge challenge) {
        List<Redeemable> redeemablesEntity = redeemables.stream()
                .map(redeemable -> this.createRedeemableToChallenge(redeemable, challenge))
                .toList();
        return redeemableRepository.saveAll(redeemablesEntity);
    }

    public Redeemable createRedeemableToChallenge(RedeemableDto redeemableDto, Challenge challenge) {
        Award award = awardService.getAwardById(redeemableDto.getAwardId());
        RedeemableId redeemableId = RedeemableId.builder()
                .challengeName(challenge.getName())
                .awardId(award.getAwardId())
                .build();
        Redeemable redeemableEntity = Redeemable.builder()
                .id(redeemableId)
                .award(award)
                .challenge(challenge)
                .limitDays(redeemableDto.getLimitDays())
                .build();
        return redeemableRepository.save(redeemableEntity);
    }

    public Redeemable updateRedeemable(String challengeName, Long awardId, RedeemableDto redeemableDto) {
        Redeemable existingRedeemable = this.getRedeemableById(challengeName, awardId);

        existingRedeemable.setLimitDays(redeemableDto.getLimitDays());

        return redeemableRepository.save(existingRedeemable);
    }

    public void deleteRedeemable(String challengeName, Long awardId) {
        Redeemable redeemable = this.getRedeemableById(challengeName, awardId);
        redeemableRepository.delete(redeemable);
    }
}