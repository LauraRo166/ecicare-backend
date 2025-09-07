package edu.escuelaing.ecicare.utils.exceptions.redeem;

import edu.escuelaing.ecicare.utils.exceptions.RedeemAwardException;

public class AwardOutOfStockException extends RedeemAwardException {

    public AwardOutOfStockException(String awardName) {
        super("Award " + awardName + " is out of stock");
    }

}
