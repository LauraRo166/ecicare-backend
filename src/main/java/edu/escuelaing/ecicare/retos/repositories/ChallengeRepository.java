package edu.escuelaing.ecicare.retos.repositories;

import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
import edu.escuelaing.ecicare.retos.models.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing {@link Challenge} entities.
 *
 * This interface extends {@link JpaRepository}, providing basic CRUD
 * operations as well as custom query methods for challenges.
 *
 * It is annotated with {@link Repository} to indicate that it is
 * a persistence layer component.
 *
 * @author Byte Programming
 */
@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, String> {

    /**
     * Finds a challenge by its unique name.
     *
     * @param name the name of the challenge
     * @return the {@link Challenge} with the specified name,
     *         or {@code null} if no match is found
     */
    Challenge findByName(String name);

    /**
     * Finds all challenges with a specific duration.
     *
     * @param duration the duration (end date/time) of the challenge
     * @return a list of {@link Challenge} entities matching the given duration
     *         or {@code null} if no match is found
     */
    List<Challenge> findByDuration(LocalDateTime duration);

    /**
     * Finds all challenges that belong to a specific health module.
     *
     * @param healthModule the health module (e.g., nutrition, exercise, etc.)
     * @return a list of {@link Challenge} entities matching the given module
     *         or {@code null} if no match is found
     */
    List<Challenge> findByHealthModule(String healthModule);

    /**
     * Finds all challenges where a specific user is registered.
     *
     * Spring Data JPA will generate the query automatically
     * based on the method name. It uses the relationship between
     * {@link Challenge} and {@link UserEcicare}.
     *
     * @param user the user to search for
     * @return a list of challenges where the given user is registered
     */
    List<Challenge> findByRegistered(UserEcicare user);
}
