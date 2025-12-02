package net.cmr.quizapp.controller;

import org.springframework.web.bind.annotation.RestController;

import net.cmr.quizapp.ApplicationProcessor;
import net.cmr.quizapp.entity.QuizResponseEntity;
import net.cmr.quizapp.entity.QuizScoreEntity;
import net.cmr.quizapp.entity.QuizScoreResponse;
import net.cmr.quizapp.entity.QuizSubmittion;
import net.cmr.quizapp.otdb.OTDBQuizResponse;
import net.cmr.quizapp.repository.QuizRepository;
import net.cmr.quizapp.service.OTDBService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class QuizRestController {

    private final OTDBService OTDBService;
    
    @Autowired
    private ApplicationProcessor applicationProcessor;

    QuizRestController(OTDBService OTDBService) {
        this.OTDBService = OTDBService;
    }

    @PostMapping("/api/quiz/opentdb")
    public Long generateCustomQuiz(@RequestBody OTDBQuizResponse quizResponse) {
        return applicationProcessor.saveOTDBQuiz(quizResponse);
    }

    @GetMapping("/api/quiz/getRandom")
    public ResponseEntity<QuizResponseEntity> getRandomQuiz() {
        return applicationProcessor.getRandomQuiz();
    }

    @GetMapping("/api/quiz/get/{quizId}")
    public ResponseEntity<QuizResponseEntity> playQuiz(@PathVariable Long quizId) {
        return applicationProcessor.generateQuizObject(quizId);
    }

    @PostMapping("/api/quiz/submit")
    public QuizScoreResponse submitQuiz(@RequestBody QuizSubmittion submittion) {
        return applicationProcessor.processQuizSubmittion(submittion);
    }

    @GetMapping("/api/categories")
    public Map<Long, String> getCategoryNameIdPairs() {
        return applicationProcessor.getCategoryNameIdPairs();
    }

    @GetMapping("/api/difficulties")
    public Map<Long, String> getDifficultyNameIdPairs() {
        return applicationProcessor.getDifficultyIdPairs();
    }   
    
    @GetMapping("/api/response-types")
    public Map<Long, String> getResponseNameIdPairs() {
        return applicationProcessor.getResponseTypeNameIdPairs();
    }  

}
