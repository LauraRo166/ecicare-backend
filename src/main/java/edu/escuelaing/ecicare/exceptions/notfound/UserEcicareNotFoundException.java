package edu.escuelaing.ecicare.exceptions.notfound;

import edu.escuelaing.ecicare.exceptions.ResourceNotFoundException;

public class UserEcicareNotFoundException extends ResourceNotFoundException {

    public UserEcicareNotFoundException(Long id) {
        super("User not found with id " + id);
    }

    public UserEcicareNotFoundException(String email) {
        super("User not found with email " + email);
    }

    public UserEcicareNotFoundException() {
        super("Invalid email or password");
    }

}
