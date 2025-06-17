package edu.escuelaing.ecicare.exceptions.notfound;

import edu.escuelaing.ecicare.exceptions.ResourceNotFoundException;

public class RedeemableNotFoundException extends ResourceNotFoundException {

    public RedeemableNotFoundException(Long challengeId, Long awardId) {
        super("Redeemable not found with challengeId: " + challengeId + " and awardId: " + awardId);
    }

}
