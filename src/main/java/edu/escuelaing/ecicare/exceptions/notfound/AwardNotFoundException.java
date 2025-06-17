package edu.escuelaing.ecicare.exceptions.notfound;

import edu.escuelaing.ecicare.exceptions.ResourceNotFoundException;

public class AwardNotFoundException extends ResourceNotFoundException {

    public AwardNotFoundException(Long id) {
        super("Award not found with id: " + id);
    }

}
