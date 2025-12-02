package net.cmr.quizapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.cmr.quizapp.entity.DifficultyEntity;
import java.util.Optional;


@Repository
public interface DifficultyRepository extends JpaRepository<DifficultyEntity, Long> {
    Optional<DifficultyEntity> findByDisplayName(String displayName);
}
