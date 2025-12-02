package net.cmr.quizapp;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.cmr.quizapp.service.OTDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.Nullable;
import net.cmr.quizapp.entity.CategoryEntity;
import net.cmr.quizapp.entity.QuestionEntity;
import net.cmr.quizapp.entity.QuizEntity;
import net.cmr.quizapp.entity.QuizResponseEntity;
import net.cmr.quizapp.entity.QuizScoreEntity;
import net.cmr.quizapp.entity.QuizScoreResponse;
import net.cmr.quizapp.entity.QuizSubmittion;
import net.cmr.quizapp.entity.UserEntity;
import net.cmr.quizapp.repository.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class ApplicationProcessor {

    private final QuizScoreRepository quizScoreRepository;

    private final ResponseTypeRepository responseTypeRepository;

    private final DifficultyRepository difficultyRepository;

    private final CategoryRepository categoryRepository;

    private final OTDBService OTDBService;

    private final ResponseRepository responseRepository;

    private final QuestionRepository questionRepository;

    private final QuizRepository quizRepository;

    public ApplicationProcessor(QuizRepository quizRepository, QuestionRepository questionRepository,
            ResponseRepository responseRepository, OTDBService OTDBService, CategoryRepository categoryRepository,
            DifficultyRepository difficultyRepository, ResponseTypeRepository responseTypeRepository,
            QuizScoreRepository quizScoreRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.responseRepository = responseRepository;
        this.OTDBService = OTDBService;
        this.categoryRepository = categoryRepository;
        this.difficultyRepository = difficultyRepository;
        this.responseTypeRepository = responseTypeRepository;
        this.quizScoreRepository = quizScoreRepository;
    }

    public Long generateRandomQuiz(@Nullable Long amount, @Nullable Long categoryId, @Nullable Long difficultyId)
            throws IllegalArgumentException {
        if (amount == null || amount <= 0 || amount >= 50) {
            amount = 10L;
        }
        QuizEntity quizEntity = OTDBService.fetchAndSaveQuiz(amount.intValue(), categoryId, difficultyId, null);
        if (quizEntity != null) {
            return quizEntity.getId();
        }
        return -1L;
    }

    public ResponseEntity<QuizResponseEntity> generateQuizObject(Long id) {
        Optional<QuizEntity> quizEntity = quizRepository.findById(id);
        if (quizEntity.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<QuestionEntity> questions = questionRepository.findByQuizId(id);
        if (questions.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        Map<Long, List<net.cmr.quizapp.entity.ResponseEntity>> questionResponses = new HashMap<>();
        for (QuestionEntity questionEntity : questions) {
            Long questionId = questionEntity.getQuestionId();
            List<net.cmr.quizapp.entity.ResponseEntity> responseList = responseRepository.findByQuestionId(questionId);
            if (responseList.isEmpty()) {
                return ResponseEntity.internalServerError().build();
            }
            questionResponses.put(questionId, responseList);
        }
        return ResponseEntity.ok(QuizResponseEntity.from(quizEntity.get(), questions, questionResponses, false));
    }

    public Map<Long, String> getCategoryNameIdPairs() {
        final Map<Long, String> categoryPairs = new HashMap<>();
        categoryRepository.findAll().forEach((entity) -> {
            categoryPairs.put(entity.getCategoryId(), entity.getDisplayName());
        });
        return categoryPairs;
    }

    public Map<Long, String> getDifficultyIdPairs() {
        final Map<Long, String> difficultyPairs = new HashMap<>();
        difficultyRepository.findAll().forEach((entity) -> {
            difficultyPairs.put(entity.getDifficultyId(), entity.getDisplayName());
        });
        return difficultyPairs;
    }

    public Map<Long, String> getResponseTypeNameIdPairs() {
        final Map<Long, String> responseTypePairs = new HashMap<>();
        responseTypeRepository.findAll().forEach((entity) -> {
            responseTypePairs.put(entity.getResponseTypeId(), entity.getDisplayText());
        });
        return responseTypePairs;
    }

    public void submitResponseToLeaderboard(QuizSubmittion submittion, QuizScoreResponse response,
            UserEntity userEntity) {
        // TODO: Do additional validation
        QuizScoreEntity quizScoreEntity = new QuizScoreEntity(userEntity.getUserId(), submittion.getQuizId(),
                response.getScore(), Timestamp.from(Instant.now()));
        quizScoreRepository.save(quizScoreEntity);
    }

    public QuizScoreResponse processQuizSubmittion(QuizSubmittion quizSubmittion) {
        Long quizId = quizSubmittion.getQuizId();
        Map<String, List<Long>> submittedAnswers = quizSubmittion.getAnswers();

        if (quizId == null) {
            throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        }

        // Validate quiz exists
        Optional<QuizEntity> quizOpt = quizRepository.findById(quizId);
        if (quizOpt.isEmpty()) {
            throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        }

        // Get all questions for this quiz
        List<QuestionEntity> questions = questionRepository.findByQuizId(quizId);
        if (questions.isEmpty()) {
            throw new IllegalStateException("No questions found for quiz ID: " + quizId);
        }

        List<Long> correctQuestions = new ArrayList<>();
        List<Long> incorrectQuestions = new ArrayList<>();

        for (QuestionEntity question : questions) {
            Long questionId = question.getQuestionId();
            int order = question.getOrder();
                String questionKey = String.valueOf(order);

            // If an answer wasn't submitted, stop
            List<Long> submittedResponseIds = submittedAnswers.get(questionKey);
            if (submittedResponseIds == null || submittedResponseIds.isEmpty()) {
                // No answer submitted - mark as incorrect
                incorrectQuestions.add(questionId);
                continue;
            }

            // Submitted answers contains the ids of the user's responses
            List<net.cmr.quizapp.entity.ResponseEntity> questionResponses = responseRepository
                    .findByQuestionId(questionId);
            List<Long> correctResponseIds = new ArrayList<>();
                for (net.cmr.quizapp.entity.ResponseEntity responseEntity : questionResponses) {
                    if (responseEntity.isCorrect()) {
                        correctResponseIds.add(responseEntity.getResponseId());
                    }
            }

            boolean isCorrect = correctResponseIds.size() == submittedResponseIds.size() &&
                    correctResponseIds.containsAll(submittedResponseIds) &&
                    submittedResponseIds.containsAll(correctResponseIds);

            if (isCorrect) {
                correctQuestions.add(questionId);
            } else {
                incorrectQuestions.add(questionId);
            }
        }

        BiFunction<Integer, Integer, Long> scoreFunction = (correct, incorrect) -> {
            int total = correct + incorrect;
            double percent = correct / (double) total;
            return (long) (percent * 1000L);
        };

        long totalScore = scoreFunction.apply(correctQuestions.size(), incorrectQuestions.size());

        // TODO: Check if user is logged in
        boolean userLoggedIn = false;

        return new QuizScoreResponse(correctQuestions, incorrectQuestions, totalScore, userLoggedIn);
    }

    public ResponseEntity<QuizResponseEntity> getRandomQuiz() {
        List<QuizEntity> allQuizzes = quizRepository.findAll();
        if (allQuizzes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        int randomIndex = (int) (Math.random() * allQuizzes.size());
        QuizEntity randomQuiz = allQuizzes.get(randomIndex);
        return generateQuizObject(randomQuiz.getId());
    }

}
