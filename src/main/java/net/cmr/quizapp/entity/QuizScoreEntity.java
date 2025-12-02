package net.cmr.quizapp.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "quiz_scores")
public class QuizScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Long scoreId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "score", nullable = false)
    private Long score;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    public QuizScoreEntity() {
    }

    public QuizScoreEntity(Long userId, Long quizId, Long score, Timestamp timestamp) {
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getScoreId() { return scoreId; }
    public void setScoreId(Long scoreId) { this.scoreId = scoreId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
