package edu.escuelaing.ecicare.premios.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import edu.escuelaing.ecicare.exceptions.notfound.RedeemableNotFoundException;
import edu.escuelaing.ecicare.premios.models.dto.RedeemableDto;
import edu.escuelaing.ecicare.premios.models.entity.Award;
import edu.escuelaing.ecicare.models.entity.Challenge;
import edu.escuelaing.ecicare.premios.models.entity.Redeemable;
import edu.escuelaing.ecicare.premios.models.entity.RedeemableId;
import edu.escuelaing.ecicare.premios.repositories.RedeemableRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedeemableService {

    private final RedeemableRepository redeemableRepository;
    private final AwardService awardService;

    public List<Redeemable> getAllRedeemables() {
        return redeemableRepository.findAll();
    }

    public Redeemable getRedeemableById(Long challengeId, Long awardId) {
        Optional<Redeemable> redeemable = redeemableRepository.findById(new RedeemableId(challengeId, awardId));
        if (!redeemable.isPresent()) throw new RedeemableNotFoundException(challengeId, awardId);
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
                .challengeId(challenge.getChallengeId())
                .awardId(award.getAwardId())
                .build();
        Redeemable redeemableEntity = Redeemable.builder()
                .id(redeemableId)
                .award(award)
                .challenge(challenge)
                .requiredQR(redeemableDto.getRequiredQR())
                .limitDays(redeemableDto.getLimitDays())
                .build();
        return redeemableRepository.save(redeemableEntity);
    }

    public Redeemable updateRedeemable(Long challengeId, Long awardId, RedeemableDto redeemableDto) {
        Redeemable existingRedeemable = this.getRedeemableById(challengeId, awardId);
        
        existingRedeemable.setRequiredQR(redeemableDto.getRequiredQR());
        existingRedeemable.setLimitDays(redeemableDto.getLimitDays());
        
        return redeemableRepository.save(existingRedeemable);
    }

    public void deleteRedeemable(Long challengeId, Long awardId) {
        Redeemable redeemable = this.getRedeemableById(challengeId, awardId);
        redeemableRepository.delete(redeemable);
    }
}