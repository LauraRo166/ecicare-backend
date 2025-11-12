package edu.escuelaing.ecicare.challenges.repositories;

import edu.escuelaing.ecicare.challenges.models.dto.ModuleGenResponse;
import edu.escuelaing.ecicare.challenges.models.entity.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
                SELECT new edu.escuelaing.ecicare.challenges.models.dto.ModuleGenResponse(
                    m.name,
                    m.imageUrl
                )
                FROM Module m
                WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    Page<ModuleGenResponse> findByNameContainingIgnoreCaseDTO(
            @Param("name") String name,
            Pageable pageable);

    @Query("""
                SELECT new edu.escuelaing.ecicare.challenges.models.dto.ModuleGenResponse(
                    m.name,
                    m.imageUrl
                )
                FROM Module m
                WHERE LOWER(m.administrator.email) = LOWER(:adminEmail)
                  AND LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    Page<ModuleGenResponse> findByAdministratorEmailDTO(
            @Param("adminEmail") String adminEmail,
            @Param("name") String name,
            Pageable pageable);
}
