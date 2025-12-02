package net.cmr.quizapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.cmr.quizapp.entity.QuizEntity;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
    
}
