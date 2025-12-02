package net.cmr.quizapp.entity;

import java.util.List;
import java.util.Map;

public class QuizSubmittion {
    
    private Long quizId;
    private Map<String, List<Long>> answers;

    public QuizSubmittion() {

    }

    public QuizSubmittion(Long quizId, Map<String, List<Long>> answers) {
        this.quizId = quizId;
        this.answers = answers;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Map<String, List<Long>> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, List<Long>> answers) {
        this.answers = answers;
    }

}
