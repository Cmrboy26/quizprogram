package net.cmr.quizapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "response_types")
public class ResponseTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_type_id")
    private Long responseTypeId;

    @Column(name = "display_text", nullable = false)
    private String displayText;

    public ResponseTypeEntity() {
    }

    public ResponseTypeEntity(String displayText) {
        this.displayText = displayText;
    }

    // Getters and Setters
    public Long getResponseTypeId() {
        return responseTypeId;
    }

    public void setResponseTypeId(Long responseTypeId) {
        this.responseTypeId = responseTypeId;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

}
