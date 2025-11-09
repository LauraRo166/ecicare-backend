package edu.escuelaing.ecicare.challenges.repositories;

import edu.escuelaing.ecicare.challenges.models.entity.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Module} entities.
 * This repository can be extended with custom query methods
 * by following Spring Data JPA's method naming conventions.
 *
 * @author Byte Programming
 */
@Repository
public interface ModuleRepository extends JpaRepository<Module, String> {
    Page<Module> findByAdministrator_Email(String email, Pageable pageable);
}
