package net.cmr.quizapp.service;

import net.cmr.quizapp.entity.*;
import net.cmr.quizapp.otdb.OTDBQuizResponse;
import net.cmr.quizapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class OTDBService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DifficultyRepository difficultyRepository;

    @Autowired
    private ResponseTypeRepository responseTypeRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public enum OTDBResponseType {
        MULTIPLE_CHOICE("multiple"),
        TRUE_FALSE("true_false")
        ;

            private final String value;

            OTDBResponseType(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }
    }

    /**
     * Fetch quiz from OpenTDB API and save to database.
     * @param amount number of questions
     * @param categoryId OpenTDB category ID (optional)
     * @param difficulty difficulty level: easy, medium, hard (optional)
     * @return saved QuizEntity
     */
    @Transactional
    public QuizEntity fetchAndSaveQuiz(int amount, Long categoryId, Long difficultyId, OTDBResponseType specificResponseType) {
        // Build OpenTDB API URL
        StringBuilder url = new StringBuilder("https://opentdb.com/api.php?amount=" + amount);
        if (categoryId != null) {
            url.append("&category=").append(categoryId);
        }
        if (difficultyId != null) {
            Optional<DifficultyEntity> difficulty = difficultyRepository.findById(difficultyId);
            if (difficulty.isPresent()) {
                url.append("&difficulty=").append(difficulty.get().getDisplayName().toLowerCase());
            }
        }
        if (specificResponseType != null) {
            url.append("&type=").append(specificResponseType.getValue());
        }

        // Fetch from OpenTDB
        OTDBQuizResponse response = restTemplate.getForObject(url.toString(), OTDBQuizResponse.class);

        if (response == null || response.getResponseCode() != 0) {
            throw new RuntimeException("Failed to fetch quiz from OpenTDB. Response code: " + 
                (response != null ? response.getResponseCode() : "null"));
        }

        // Create and save quiz
        QuizEntity quiz = new QuizEntity();
        quiz.setName("OpenTDB Quiz - " + System.currentTimeMillis());
        quiz.setDescription("Quiz fetched from Open Trivia Database");
        
        quiz = quizRepository.save(quiz);

        int order = 1;
        for (OTDBQuizResponse.OTDBQuestion otdbQ : response.getResults()) {
            QuestionEntity question = new QuestionEntity();
            question.setQuizId(quiz.getId());
            question.setPrompt(decodeHtml(otdbQ.getQuestion()));
            question.setOrder(order++);
            question.setCategoryId(resolveCategoryId(otdbQ.getCategory()));
            question.setResponseTypeId(resolveResponseTypeId(otdbQ.getType()));
            question.setDifficultyId(resolveDifficultyId(otdbQ.getDifficulty()));
            
            question = questionRepository.save(question);

            List<String> allAnswers = new ArrayList<>(otdbQ.getIncorrectAnswers());
            allAnswers.add(otdbQ.getCorrectAnswer());
            Collections.shuffle(allAnswers);

            for (String answer : allAnswers) {
                ResponseEntity responseEntity = new ResponseEntity();
                responseEntity.setQuestionId(question.getQuestionId());
                responseEntity.setText(decodeHtml(answer));
                responseEntity.setCorrect(answer.equals(otdbQ.getCorrectAnswer()));
                responseRepository.save(responseEntity);
            }
        }

        return quiz;
    }

    private Long resolveCategoryId(String categoryName) {
        categoryName = decodeHtml(categoryName);
        Optional<CategoryEntity> category = categoryRepository.findByDisplayName(categoryName);
        if (category.isPresent()) {
            return category.get().getCategoryId();
        }
        throw new IllegalArgumentException("Category not found: "+categoryName);
    }

    private Long resolveDifficultyId(String difficultyName) {
        Optional<DifficultyEntity> difficulty = difficultyRepository.findByDisplayName(difficultyName.toLowerCase());
        if (difficulty.isPresent()) {
            return difficulty.get().getDifficultyId();
        }
        throw new IllegalArgumentException("Difficulty not found: " + difficultyName);
    }

    private Long resolveResponseTypeId(String type) {
        // Map "multiple" or "boolean" to response type
        String displayText = type.equals(OTDBResponseType.MULTIPLE_CHOICE.getValue()) ? "Multiple Choice" : "True False";
        Optional<ResponseTypeEntity> responseEntity = responseTypeRepository.findByDisplayText(displayText);
        if (responseEntity.isPresent()) {
            return responseEntity.get().getResponseTypeId();
        }
        throw new IllegalArgumentException("Response type not found: "+displayText);
    }

    private String decodeHtml(String text) {
        return text
            .replace("&quot;", "\"")
            .replace("&amp;", "&")
            .replace("&#039;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&apos;", "'");
    }
}
