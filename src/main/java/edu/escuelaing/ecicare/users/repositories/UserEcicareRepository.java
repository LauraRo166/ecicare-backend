package edu.escuelaing.ecicare.users.repositories;

import edu.escuelaing.ecicare.users.models.entity.UserEcicare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserEcicareRepository extends JpaRepository<UserEcicare, Long> {
    Optional<UserEcicare> findByEmail(String email);
}
