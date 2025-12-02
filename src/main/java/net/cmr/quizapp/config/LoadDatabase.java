package net.cmr.quizapp.config;
import net.cmr.quizapp.repository.ResponseTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import net.cmr.quizapp.ApplicationProcessor;
import net.cmr.quizapp.entity.CategoryEntity;
import net.cmr.quizapp.entity.DifficultyEntity;
import net.cmr.quizapp.entity.QuizEntity;
import net.cmr.quizapp.entity.ResponseTypeEntity;
import net.cmr.quizapp.otdb.OTDBCategories;
import net.cmr.quizapp.otdb.OTDBCategories.OTDBCategory;
import net.cmr.quizapp.repository.CategoryRepository;
import net.cmr.quizapp.repository.DifficultyRepository;
import net.cmr.quizapp.repository.QuizRepository;

@Configuration
public class LoadDatabase {

    private final ResponseTypeRepository responseTypeRepository;

    private final CategoryRepository categoryRepository;

    private final DifficultyRepository difficultyRepository;

    private final ApplicationProcessor applicationProcessor;
    
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    LoadDatabase(ApplicationProcessor applicationProcessor, DifficultyRepository difficultyRepository, CategoryRepository categoryRepository, ResponseTypeRepository responseTypeRepository) {
        this.applicationProcessor = applicationProcessor;
        this.difficultyRepository = difficultyRepository;
        this.categoryRepository = categoryRepository;
        this.responseTypeRepository = responseTypeRepository;
    }

    @Bean
    CommandLineRunner initDatabase(QuizRepository quizRepository) {
        return args -> {
            // Preload difficulties
            log.info("Preloading \"Easy\" difficulty...");
            difficultyRepository.save(new DifficultyEntity("easy"));
            log.info("Preloading \"Medium\" difficulty...");
            DifficultyEntity mediumDifficulty = difficultyRepository.save(new DifficultyEntity("medium"));
            log.info("Preloading \"Hard\" difficulty...");
            difficultyRepository.save(new DifficultyEntity("hard"));

            // Retrieve categories from Open Trivia Database
            final String categoriesURI = "https://opentdb.com/api_category.php";

            RestTemplate restTemplate = new RestTemplate();
            OTDBCategories categoryResult = restTemplate.getForObject(categoriesURI, OTDBCategories.class);
            log.info("Read category list from Open Trivia Database: \n"+categoryResult);

            // Register categories to our database
            CategoryEntity latestCategoryEntity = null;
            for (OTDBCategory category : categoryResult.getTriviaCategories()) {
                log.info(String.format("Preloading OTDB category \"%s\"...", category.getName()));
                latestCategoryEntity = categoryRepository.save(new CategoryEntity(category.getName()));
            }

            // Register valid quiz types
            log.info("Preload \"Multiple Choice\" response type...");
            responseTypeRepository.save(new ResponseTypeEntity("Multiple Choice"));

            log.info("Preload \"Multiple Select\" response type...");
            responseTypeRepository.save(new ResponseTypeEntity("Multiple Select"));

            log.info("Preload \"True False\" response type...");
            responseTypeRepository.save(new ResponseTypeEntity("True False"));

            log.info("All categories loaded");

            log.info("Loading generated quiz");
            Long quizId = applicationProcessor.generateRandomQuiz(10L, null, null);
            log.info("Generated quiz! Id: "+quizId);

            // Periodically generate random quizzes
            /*new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(10000); // Wait 60 seconds
                        Long periodicQuizId = applicationProcessor.generateRandomQuiz(10L, null, null);
                        log.info("Periodically generated quiz! Id: " + periodicQuizId);
                    } catch (InterruptedException e) {
                        log.error("Quiz generation thread interrupted", e);
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }).start();*/
        };
    }

}
