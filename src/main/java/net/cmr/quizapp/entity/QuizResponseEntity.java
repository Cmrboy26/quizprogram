package net.cmr.quizapp.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;

/**
 * Serializable DTO sent to clients containing full quiz structure.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizResponseEntity implements Serializable {

	@JsonProperty("quiz_id")
	private Long quizId;

	private String name;
	private String description;

	@JsonProperty("category_id")
	private Long categoryId;

	@JsonProperty("difficulty_id")
	private Long difficultyId;

	private List<Question> questions;

	public QuizResponseEntity() {}

	public QuizResponseEntity(Long quizId,
							  String name,
							  String description,
							  Long categoryId,
							  Long difficultyId,
							  List<Question> questions) {
		this.quizId = quizId;
		this.name = name;
		this.description = description;
		this.categoryId = categoryId;
		this.difficultyId = difficultyId;
		this.questions = questions;
	}

	/**
	 * Build a QuizResponseEntity from persisted entities.
	 * @param quiz QuizEntity root
	 * @param questionEntities list of questions belonging to the quiz
	 * @param responsesByQuestion map of questionId -> list of responses
	 * @param includeCorrect whether to include correctness flags in responses
	 */
	public static QuizResponseEntity from(QuizEntity quiz,
										  List<QuestionEntity> questionEntities,
										  Map<Long, List<ResponseEntity>> responsesByQuestion,
										  boolean includeCorrect) {
		List<Question> questionDtos = questionEntities.stream()
			.sorted((a,b) -> Integer.compare(a.getOrder(), b.getOrder()))
			.map(q -> new Question(
					q.getQuestionId(),
					q.getPrompt(),
					q.getOrder(),
					q.getResponseTypeId(),
					q.getDifficultyId(),
                    q.getCategoryId(),
					buildResponses(responsesByQuestion.getOrDefault(q.getQuestionId(), List.of()), includeCorrect)
			))
			.collect(Collectors.toList());

		return new QuizResponseEntity(
				quiz.getId(),
				quiz.getName(),
				quiz.getDescription(),
				quiz.getCategoryId(),
				quiz.getDifficultyId(),
				questionDtos
		);
	}

	private static List<Response> buildResponses(List<ResponseEntity> entities, boolean includeCorrect) {
		return entities.stream()
				.map(r -> new Response(r.getResponseId(), r.getText(), includeCorrect ? r.isCorrect() : null))
				.collect(Collectors.toList());
	}

	// Getters
	public Long getQuizId() { return quizId; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public Long getCategoryId() { return categoryId; }
	public Long getDifficultyId() { return difficultyId; }
	public List<Question> getQuestions() { return questions; }

	// Nested DTOs
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Question implements Serializable {
		@JsonProperty("question_id")
		private Long questionId;
		private String prompt;
		@JsonProperty("order")
		private int order;
		@JsonProperty("response_type_id")
		private Long responseTypeId;
		@JsonProperty("difficulty_id")
		private Long difficultyId;
        @JsonProperty("category_id")
        private Long categoryId;
		private List<Response> responses;

		public Question() {}
		public Question(Long questionId, String prompt, int order, Long responseTypeId, Long difficultyId, Long categoryId, List<Response> responses) {
			this.questionId = questionId;
			this.prompt = prompt;
			this.order = order;
			this.responseTypeId = responseTypeId;
			this.difficultyId = difficultyId;
            this.categoryId = categoryId;
			this.responses = responses;
		}
		public Long getQuestionId() { return questionId; }
		public String getPrompt() { return prompt; }
		public int getOrder() { return order; }
		public Long getResponseTypeId() { return responseTypeId; }
		public Long getDifficultyId() { return difficultyId; }
        public Long getCategoryId() { return categoryId; }
		public List<Response> getResponses() { return responses; }
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Response implements Serializable {
		@JsonProperty("response_id")
		private Long responseId;
		private String text;
		@JsonProperty("is_correct")
		private Boolean isCorrect; // nullable when hidden

		public Response() {}
		public Response(Long responseId, String text, Boolean isCorrect) {
			this.responseId = responseId;
			this.text = text;
			this.isCorrect = isCorrect;
		}
		public Long getResponseId() { return responseId; }
		public String getText() { return text; }
		public Boolean getIsCorrect() { return isCorrect; }
	}
}
