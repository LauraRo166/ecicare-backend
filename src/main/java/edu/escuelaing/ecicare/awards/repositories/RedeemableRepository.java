package edu.escuelaing.ecicare.awards.repositories;

import edu.escuelaing.ecicare.awards.models.entity.Redeemable;
import edu.escuelaing.ecicare.awards.models.entity.RedeemableId;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedeemableRepository extends JpaRepository<Redeemable, RedeemableId> {

    Optional<Redeemable> findById_ChallengeNameAndId_AwardId(String challengeName, Long awardId);

    List<Redeemable> findByChallenge_Name(String challengeName);
    
}