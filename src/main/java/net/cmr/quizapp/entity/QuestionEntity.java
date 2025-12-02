package net.cmr.quizapp.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "response_type_id", nullable = false)
    private Long responseTypeId;

    @Column(name = "difficulty_id", nullable = false)
    private Long difficultyId;

    @Column(name = "category_id", nullable = true)
    private Long categoryId;

    @Column(name = "order_num", nullable = false)
    private int order;

    @Column(name = "prompt", nullable = false)
    private String prompt;

    public QuestionEntity() {

    }

    public QuestionEntity(Long quizId, Long responseTypeId, Long difficultyId, @Nullable Long categoryId, int order, String prompt) {
        this.quizId = quizId;
        this.responseTypeId = responseTypeId;
        this.difficultyId = difficultyId;
        this.categoryId = categoryId;
        this.order = order;
        this.prompt = prompt;
    }

    // Getters and Setters
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Long getResponseTypeId() {
        return responseTypeId;
    }

    public void setResponseTypeId(Long responseTypeId) {
        this.responseTypeId = responseTypeId;
    }

    public Long getDifficultyId() {
        return difficultyId;
    }

    public void setDifficultyId(Long difficultyId) {
        this.difficultyId = difficultyId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
