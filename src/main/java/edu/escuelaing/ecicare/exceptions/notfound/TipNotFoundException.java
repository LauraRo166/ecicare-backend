package edu.escuelaing.ecicare.exceptions.notfound;

import edu.escuelaing.ecicare.exceptions.ResourceNotFoundException;

public class TipNotFoundException extends ResourceNotFoundException {

    public TipNotFoundException(Long id) {
        super("Tip not found with id " + id);
    }

}
