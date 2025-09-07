package edu.escuelaing.ecicare.utils.exceptions.notfound;

import edu.escuelaing.ecicare.utils.exceptions.ResourceNotFoundException;

public class RedeemableNotFoundException extends ResourceNotFoundException {

    public RedeemableNotFoundException(String challengeName, Long awardId) {
        super("Redeemable not found with challengeId: " + challengeName + " and awardId: " + awardId);
    }

}
