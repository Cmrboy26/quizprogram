package net.cmr.quizapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "responses")
public class ResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long responseId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;

    public ResponseEntity() {
    }

    public ResponseEntity(Long questionId, String text, boolean isCorrect) {
        this.questionId = questionId;
        this.text = text;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public Long getResponseId() { return responseId; }
    public void setResponseId(Long responseId) { this.responseId = responseId; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
