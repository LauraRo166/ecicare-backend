package edu.escuelaing.ecicare.utils.exceptions.notfound;

import edu.escuelaing.ecicare.utils.exceptions.ResourceNotFoundException;

public class ModuleNotFoundException extends ResourceNotFoundException {

    public ModuleNotFoundException(Long id) {
        super("Module not found with id " + id);
    }

}
