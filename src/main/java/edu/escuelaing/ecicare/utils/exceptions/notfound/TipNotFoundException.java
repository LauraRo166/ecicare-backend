package edu.escuelaing.ecicare.utils.exceptions.notfound;

import edu.escuelaing.ecicare.utils.exceptions.ResourceNotFoundException;

public class TipNotFoundException extends ResourceNotFoundException {

    public TipNotFoundException(Long id) {
        super("Tip not found with id " + id);
    }

}
