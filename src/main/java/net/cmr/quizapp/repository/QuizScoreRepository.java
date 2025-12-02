package net.cmr.quizapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.cmr.quizapp.entity.QuizScoreEntity;

@Repository
public interface QuizScoreRepository extends JpaRepository<QuizScoreEntity, Long> {
    
}
