package edu.escuelaing.ecicare.exceptions.notfound;

import edu.escuelaing.ecicare.exceptions.ResourceNotFoundException;

public class ModuleNotFoundException extends ResourceNotFoundException {

    public ModuleNotFoundException(Long id) {
        super("Module not found with id " + id);
    }

}
