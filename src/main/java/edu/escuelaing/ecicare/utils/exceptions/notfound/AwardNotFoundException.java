package edu.escuelaing.ecicare.utils.exceptions.notfound;

import edu.escuelaing.ecicare.utils.exceptions.ResourceNotFoundException;

public class AwardNotFoundException extends ResourceNotFoundException {

    public AwardNotFoundException(Long id) {
        super("Award not found with id: " + id);
    }

}
