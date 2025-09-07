package edu.escuelaing.ecicare.premios.repositories;

import edu.escuelaing.ecicare.premios.models.entity.Redeemable;
import edu.escuelaing.ecicare.premios.models.entity.RedeemableId;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedeemableRepository extends JpaRepository<Redeemable, RedeemableId> {

    Optional<Redeemable> findByChallengeChallengeIdAndAwardAwardId(String challengeName, Long awardId);
    
}