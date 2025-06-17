package edu.escuelaing.ecicare.exceptions.redeem;

import edu.escuelaing.ecicare.exceptions.RedeemAwardException;

public class AwardOutOfStockException extends RedeemAwardException {

    public AwardOutOfStockException(String awardName) {
        super("Award " + awardName + " is out of stock");
    }

}
