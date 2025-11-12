package edu.escuelaing.ecicare.awards.repositories;

import edu.escuelaing.ecicare.awards.models.entity.Redeemable;
import edu.escuelaing.ecicare.awards.models.entity.RedeemableId;

import java.util.List;
import java.util.Optional;

import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Redeemable} entities.
 *
 * Extends {@link JpaRepository} to provide CRUD operations and defines
 * custom query methods for composite key lookups and challenge-based filtering.
 */
@Repository
public interface RedeemableRepository extends JpaRepository<Redeemable, RedeemableId> {

    /**
     * Finds a {@link Redeemable} by its composite identifier
     * consisting of {@code challengeName} and {@code awardId}.
     *
     * @param challengeName the name of the challenge linked to the redeemable
     * @param awardId the identifier of the award linked to the redeemable
     * @return an {@link Optional} containing the {@link Redeemable} if found,
     *         or empty if not found
     */
    Optional<Redeemable> findById_ChallengeNameAndId_AwardId(String challengeName, Long awardId);

    /**
     * Retrieves all {@link Redeemable} entities associated with a given challenge.
     *
     * @param challengeName the name of the challenge
     * @return a list of {@link Redeemable} linked to the specified challenge
     */
    List<Redeemable> findByChallenge_Name(String challengeName);

    void deleteAllByChallenge(Challenge challenge);
}