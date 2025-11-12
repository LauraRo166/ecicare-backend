package edu.escuelaing.ecicare.awards.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import edu.escuelaing.ecicare.utils.exceptions.notfound.RedeemableNotFoundException;
import edu.escuelaing.ecicare.awards.models.dto.RedeemableDto;
import edu.escuelaing.ecicare.awards.models.entity.Award;
import edu.escuelaing.ecicare.awards.models.entity.Redeemable;
import edu.escuelaing.ecicare.awards.models.entity.RedeemableId;
import edu.escuelaing.ecicare.awards.repositories.RedeemableRepository;
import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
import edu.escuelaing.ecicare.challenges.services.ChallengeService;
import lombok.RequiredArgsConstructor;

/**
 * Service layer for handling business logic related to {@link Redeemable}
 * entities.
 *
 * Provides CRUD operations, creation of relationships between {@link Challenge}
 * and {@link Award}, and repository access methods.
 */
@Service
@RequiredArgsConstructor
public class RedeemableService {

    private final RedeemableRepository redeemableRepository;
    private final AwardService awardService;
    private final ChallengeService challengeService;

    /**
     * Retrieves all redeemable entities from the repository.
     *
     * @return a list of all {@link Redeemable} entities
     */
    public List<Redeemable> getAllRedeemables() {
        return redeemableRepository.findAll();
    }

    /**
     * Retrieves a redeemable by its composite ID.
     *
     * @param challengeName the name of the challenge
     * @param awardId       the ID of the award
     * @return the matching {@link Redeemable} entity
     * @throws RedeemableNotFoundException if the redeemable does not exist
     */
    public Redeemable getRedeemableById(String challengeName, Long awardId) {
        Optional<Redeemable> redeemable = redeemableRepository.findById(new RedeemableId(challengeName, awardId));
        if (!redeemable.isPresent())
            throw new RedeemableNotFoundException(challengeName, awardId);
        return redeemable.get();
    }

    /**
     * Creates multiple redeemables linked to challenges based on a list of DTOs.
     *
     * @param redeemables list of {@link RedeemableDto} objects containing data
     * @return a list of created {@link Redeemable} entities
     */
    public List<Redeemable> createRedeemablesToChallenge(List<RedeemableDto> redeemables) {
        List<Redeemable> redeemablesEntity = redeemables.stream()
                .map(this::createRedeemableToChallenge)
                .toList();
        return redeemableRepository.saveAll(redeemablesEntity);
    }

    /**
     * Creates a single redeemable entity linked to a specific challenge and award.
     *
     * @param redeemableDto the DTO containing data for the redeemable
     * @return the created {@link Redeemable} entity
     */
    public Redeemable createRedeemableToChallenge(RedeemableDto redeemableDto) {
        Award award = awardService.getAwardById(redeemableDto.getAwardId());
        Challenge challenge = challengeService.getChallengeEntityByName(redeemableDto.getChallengeName());

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

    /**
     * Updates the details of an existing redeemable.
     *
     * @param challengeName the name of the challenge
     * @param awardId       the ID of the award
     * @param redeemableDto the DTO with updated details
     * @return the updated {@link Redeemable} entity
     * @throws RedeemableNotFoundException if the redeemable does not exist
     */
    public Redeemable updateRedeemable(String challengeName, Long awardId, RedeemableDto redeemableDto) {
        Redeemable existingRedeemable = this.getRedeemableById(challengeName, awardId);
        if (redeemableDto.getLimitDays() != null) {
            existingRedeemable.setLimitDays(redeemableDto.getLimitDays());
        }
        return redeemableRepository.save(existingRedeemable);
    }

    /**
     * Deletes a redeemable entity by its composite ID.
     *
     * @param challengeName the name of the challenge
     * @param awardId       the ID of the award
     * @throws RedeemableNotFoundException if the redeemable does not exist
     */
    public void deleteRedeemable(String challengeName, Long awardId) {
        Redeemable redeemable = this.getRedeemableById(challengeName, awardId);
        redeemableRepository.delete(redeemable);
    }
}
