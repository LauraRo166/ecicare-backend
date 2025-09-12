package edu.escuelaing.ecicare.premios.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.escuelaing.ecicare.premios.models.entity.Award;

@Repository
public interface AwardRepository extends JpaRepository<Award, Long> {
}