package net.cmr.quizapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "difficulties")
public class DifficultyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "difficulty_id")
    private Long difficultyId;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    public DifficultyEntity() {
    }

    public DifficultyEntity(String displayName) {
        this.displayName = displayName;
    }

    // Getters and Setters
    public Long getDifficultyId() {
        return difficultyId;
    }

    public void setDifficultyId(Long difficultyId) {
        this.difficultyId = difficultyId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
