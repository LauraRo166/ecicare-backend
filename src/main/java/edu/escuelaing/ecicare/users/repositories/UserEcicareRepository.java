package edu.escuelaing.ecicare.users.repositories;

import edu.escuelaing.ecicare.users.models.entity.UserEcicare;
import edu.escuelaing.ecicare.utils.models.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link UserEcicare} entities.
 * Extends {@link JpaRepository} to provide CRUD operations and query methods.
 */
@Repository
public interface UserEcicareRepository extends JpaRepository<UserEcicare, Long> {

    /**
     * Finds a user by their email.
     *
     * @param email The email of the user to be searched.
     * @return An {@link Optional} containing the user if found, or empty otherwise.
     */
    Optional<UserEcicare> findByEmail(String email);

    List<UserEcicare> findByNameContainingIgnoreCase(String name);

    List<UserEcicare> findByNameContainingIgnoreCaseAndRole(String name, Role role);

}
