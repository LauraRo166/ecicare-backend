package edu.escuelaing.ecicare.utils.exceptions.redeem;

import edu.escuelaing.ecicare.utils.exceptions.RedeemAwardException;

public class NotEnoughQRQuantityException extends RedeemAwardException {

    public NotEnoughQRQuantityException(Integer difference) {
        super("Not enough QR quantity, you need " + difference + " more");
    }

}
