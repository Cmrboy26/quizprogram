package net.cmr.quizapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.cmr.quizapp.entity.ResponseTypeEntity;

@Repository
public interface ResponseTypeRepository extends JpaRepository<ResponseTypeEntity, Long> {
    Optional<ResponseTypeEntity> findByDisplayText(String displayText);
}
