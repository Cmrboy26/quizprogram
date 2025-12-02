package net.cmr.quizapp.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuizScoreResponse {

    @JsonProperty("correct_questions")
    private List<Long> correctQuestions;
    @JsonProperty("incorrect_questions")
    private List<Long> incorrectQuestions;
    @JsonProperty("score")
    private Long score;
    @JsonProperty("score_submitted")
    private boolean scoreSubmitted;

    public QuizScoreResponse() {
    }

    public QuizScoreResponse(List<Long> correctQuestions, List<Long> incorrectQuestions, Long score, boolean scoreSubmitted) {
        this.correctQuestions = correctQuestions;
        this.incorrectQuestions = incorrectQuestions;
        this.score = score;
        this.scoreSubmitted = scoreSubmitted;
    }

    public List<Long> getCorrectQuestions() {
        return correctQuestions;
    }

    public void setCorrectQuestions(List<Long> correctQuestions) {
        this.correctQuestions = correctQuestions;
    }

    public List<Long> getIncorrectQuestions() {
        return incorrectQuestions;
    }

    public void setIncorrectQuestions(List<Long> incorrectQuestions) {
        this.incorrectQuestions = incorrectQuestions;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public boolean isScoreSubmitted() {
        return scoreSubmitted;
    }

    public void setScoreSubmitted(boolean scoreSubmitted) {
        this.scoreSubmitted = scoreSubmitted;
    }

}
