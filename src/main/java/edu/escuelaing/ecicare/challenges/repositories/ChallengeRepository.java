package edu.escuelaing.ecicare.challenges.repositories;

import edu.escuelaing.ecicare.users.models.entity.UserEcicare;
import edu.escuelaing.ecicare.challenges.models.dto.UserEmailNameDTO;
import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

        Page<Challenge> findByModule_Name(String id, Pageable pageable);

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

        /**
         * Finds ALL challenges by name using case-insensitive partial matching without
         * pagination.
         * Perfect for search functionality where all matching results are needed
         * and will be grouped by modules.
         * 
         * @param name the search term to match (case-insensitive, partial matching)
         * @return a list of ALL challenges matching the search criteria, sorted by name
         */
        List<Challenge> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

        @Query("SELECT CASE WHEN :user MEMBER OF c.registered THEN true ELSE false END " +
                        "FROM Challenge c WHERE c = :challenge")
        boolean isUserRegistered(@Param("user") UserEcicare user, @Param("challenge") Challenge challenge);

        /**
         * Finds ALL challenges by name using case-insensitive partial matching without
         * pagination.
         * Perfect for search functionality where all matching results are needed
         * and will be grouped by modules.
         * 
         * @param name the search term to match (case-insensitive, partial matching)
         * @return a list of ALL challenges matching the search criteria, sorted by name
         */
        @Query("""
                        SELECT new edu.escuelaing.ecicare.challenges.models.dto.UserEmailNameDTO(
                            u.email, u.name
                        )
                        FROM UserEcicare u
                        JOIN u.challengesRegistered c
                        WHERE c.name = :challengeName
                          AND (
                               LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                          )
                        """)
        Page<UserEmailNameDTO> searchRegisteredUsers(
                        @Param("challengeName") String challengeName,
                        @Param("search") String search,
                        Pageable pageable);

        Page<Challenge> findByNameContainingIgnoreCase(String name, Pageable pageable);

        /**
         * Finds challenges by name (partial, case-insensitive) and belonging to a
         * specific module.
         * Useful for filtering search results within a module.
         *
         * @param name       search term for challenge name
         * @param moduleName name of the module
         * @param pageable   pagination information
         * @return a page of challenges matching the criteria
         */
        Page<Challenge> findByNameContainingIgnoreCaseAndModule_Name(String name, String moduleName, Pageable pageable);
        @Query("""
            SELECT c FROM Challenge c
            JOIN c.confirmed u
            WHERE u.idEci = :userId
              AND LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
        """)
        Page<Challenge> findConfirmedChallengesByUserIdAndSearch(
                        @Param("userId") Long userId,
                        @Param("search") String search,
                        Pageable pageable);
        @Query("""
            SELECT c FROM Challenge c
            JOIN c.registered u
            WHERE u.idEci = :userId
              AND LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
        """)
        Page<Challenge> findRegisterChallengesByUserIdAndSearch(
                @Param("userId") Long userId,
                @Param("search") String search,
                Pageable pageable);

        @Query("""
                SELECT c.module.administrator FROM Challenge c
                WHERE c.name = :name
                """)
        UserEcicare findChallengeAdministrator(@Param("name") String name);
        @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END " +
                "FROM Challenge c JOIN c.registered u " +
                "WHERE c.name = :challengeName AND u.idEci = :userId")
        boolean isUserRegisteredInChallenge(@Param("challengeName") String challengeName,
                                            @Param("userId") Long userId);

        /**
         * Verifica si un usuario ha completado (confirmado) un challenge.
         *
         * @param challengeName nombre del reto
         * @param userId id del usuario (UserEcicare.idEci)
         * @return true si el usuario ha completado el reto, false en caso contrario
         */
        @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END " +
                "FROM Challenge c JOIN c.confirmed u " +
                "WHERE c.name = :challengeName AND u.idEci = :userId")
        boolean isUserConfirmedInChallenge(@Param("challengeName") String challengeName,
                                           @Param("userId") Long userId);
}
