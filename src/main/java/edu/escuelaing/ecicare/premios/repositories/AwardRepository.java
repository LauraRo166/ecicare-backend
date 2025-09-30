package edu.escuelaing.ecicare.premios.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.escuelaing.ecicare.premios.models.entity.Award;

@Repository
public interface AwardRepository extends JpaRepository<Award, Long> {

    /**
     * Finds awards whose name contains the specified text with pagination.
     * Optimized for real-time search with large datasets.
     * 
     * @param name     the text to search for in award names
     * @param pageable pagination information
     * @return page of awards matching the search criteria
     */
    Page<Award> findByNameContainingIgnoreCase(String name, Pageable pageable);
}