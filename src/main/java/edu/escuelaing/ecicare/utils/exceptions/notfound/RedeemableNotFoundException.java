package edu.escuelaing.ecicare.utils.exceptions.notfound;

import edu.escuelaing.ecicare.utils.exceptions.ResourceNotFoundException;

public class RedeemableNotFoundException extends ResourceNotFoundException {

    public RedeemableNotFoundException(Long challengeId, Long awardId) {
        super("Redeemable not found with challengeId: " + challengeId + " and awardId: " + awardId);
    }

}
