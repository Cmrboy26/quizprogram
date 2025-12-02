package net.cmr.quizapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.cmr.quizapp.entity.QuestionEntity;
import net.cmr.quizapp.entity.ResponseEntity;

@Repository
public interface ResponseRepository extends JpaRepository<ResponseEntity, Long> {
    List<ResponseEntity> findByQuestionId(Long questionId);
}
