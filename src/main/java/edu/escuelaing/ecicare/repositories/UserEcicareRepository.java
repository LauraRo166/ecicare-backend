package edu.escuelaing.ecicare.repositories;

import edu.escuelaing.ecicare.models.entity.UserEcicare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEcicareRepository extends JpaRepository<UserEcicare, Long> {
}
