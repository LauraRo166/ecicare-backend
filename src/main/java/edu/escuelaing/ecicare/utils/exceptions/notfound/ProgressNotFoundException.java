package edu.escuelaing.ecicare.utils.exceptions.notfound;

import edu.escuelaing.ecicare.utils.exceptions.ResourceNotFoundException;

public class ProgressNotFoundException extends ResourceNotFoundException {

    public ProgressNotFoundException(Long idEci, Long challengeId) {
        super("Progress not found for user ecicare with idEci: " + idEci + " and challenge with id: " + challengeId);
    }

}
