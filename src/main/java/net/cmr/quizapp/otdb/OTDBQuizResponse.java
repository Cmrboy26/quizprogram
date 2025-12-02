package net.cmr.quizapp.otdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO for parsing OpenTDB API quiz response.
 */
public class OTDBQuizResponse {

    @JsonProperty("response_code")
    private int responseCode;

    private List<OTDBQuestion> results;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public List<OTDBQuestion> getResults() {
        return results;
    }

    public void setResults(List<OTDBQuestion> results) {
        this.results = results;
    }

    public static class OTDBQuestion {
        private String type;
        private String difficulty;
        private String category;
        private String question;

        @JsonProperty("correct_answer")
        private String correctAnswer;

        @JsonProperty("incorrect_answers")
        private List<String> incorrectAnswers;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public List<String> getIncorrectAnswers() {
            return incorrectAnswers;
        }

        public void setIncorrectAnswers(List<String> incorrectAnswers) {
            this.incorrectAnswers = incorrectAnswers;
        }
    }
}
