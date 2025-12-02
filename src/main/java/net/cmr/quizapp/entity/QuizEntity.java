package net.cmr.quizapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz")
public class QuizEntity {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "category_id", nullable = true)
    private Long categoryId;

    @Column(name = "difficulty_id", nullable = true)
    private Long difficultyId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    public QuizEntity() { }

    public QuizEntity(String name, String description, Long categoryId, Long difficultyId) {
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.difficultyId = difficultyId;
    }

    public QuizEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getDifficultyId() {
        return difficultyId;
    }

    public void setDifficultyId(Long difficultyId) {
        this.difficultyId = difficultyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
