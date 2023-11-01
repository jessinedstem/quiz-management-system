package com.example.assignment1.quizmanagement.service;

import com.example.assignment1.quizmanagement.contract.QuestionRequest;
import com.example.assignment1.quizmanagement.contract.QuestionResponse;
import com.example.assignment1.quizmanagement.contract.QuizRequest;
import com.example.assignment1.quizmanagement.contract.QuizResponse;
import com.example.assignment1.quizmanagement.exception.EntityNotFoundException;
import com.example.assignment1.quizmanagement.model.Question;
import com.example.assignment1.quizmanagement.model.Quiz;
import com.example.assignment1.quizmanagement.repository.QuestionRepository;
import com.example.assignment1.quizmanagement.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class QuizServiceTest {
    @InjectMocks
    private QuizService quizService;
    @Mock
    private QuizRepository quizRepository;
    @Mock
    private QuestionRepository questionRepository;
@BeforeEach
public void init(){
    MockitoAnnotations.openMocks(this);
}
    @Test
    public void testGetAllQuizzes()throws Exception {
        Question question1 = Question.builder()
                .id(1L)
                .question("Question 1")
                .build();
        Question question2 = Question.builder()
                .id(2L)
                .question("Question 2")
                .build();
        List<Question> questions = new ArrayList<>();
        questions.add(question1);
        questions.add(question2);

        Quiz quiz1 = Quiz.builder()
                .id(1L)
                .category("Category 1")
                .questions(questions)
                .build();
        Quiz quiz2 = Quiz.builder()
                .id(2L)
                .category("Category 2")
                .questions(new ArrayList<>())
                .build();
        List<Quiz> quizzes = new ArrayList<>();
        quizzes.add(quiz1);
        quizzes.add(quiz2);

        when(quizRepository.findAll()).thenReturn(quizzes);
        QuizService quizService = new QuizService(quizRepository, null);
        List<QuizResponse> result = quizService.findAllQuizzes();
        assertEquals(2, result.size());
        QuizResponse resultQuiz1 = result.get(0);
        assertEquals(1L, resultQuiz1.getId());
        assertEquals("Category 1", resultQuiz1.getCategory());
        assertEquals(2, resultQuiz1.getQuestions().size());

        QuizResponse resultQuiz2 = result.get(1);
        assertEquals(2L, resultQuiz2.getId());
        assertEquals("Category 2", resultQuiz2.getCategory());
        assertEquals(0, resultQuiz2.getQuestions().size());
    }
    @Test
    void testFindAllQuizzes_throwsException(){
    List<Quiz> quizzes=new ArrayList<>();
        when(quizRepository.findAll()).thenReturn(quizzes);
    RuntimeException exception=assertThrows(RuntimeException.class, () -> {
        quizService.findAllQuizzes();
    });
        assertEquals("Quizzes not found",exception.getMessage());
}
    @Test
    void testCreateQuiz()throws Exception {
               QuizRequest quizRequest = QuizRequest.builder()
                .category("Category 1")
                .questions(Collections.singletonList(QuestionRequest.builder().question("Question 1").build()))
                .build();
        Quiz expectedQuiz = Quiz.builder()
                .id(1L)
                .category(quizRequest.getCategory())
                .questions(new ArrayList<>())
                .build();
        when(quizRepository.save(any(Quiz.class))).thenReturn(expectedQuiz);
        QuizResponse actualResponse = quizService.createQuiz(quizRequest);
        assertEquals(expectedQuiz.getId(), actualResponse.getId());
        assertEquals(expectedQuiz.getCategory(), actualResponse.getCategory());
            }
    @Test
    public void testGetQuizById() throws Exception{
        long quizId = 1L;
        Quiz quiz = Quiz.builder()
                .id(quizId)
                .category("Category 1")
                .questions(new ArrayList<>())
                .build();

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        QuizService quizService = new QuizService(quizRepository, null);
        QuizResponse result = quizService.getQuizById(quizId);
        assertEquals(quizId, result.getId());
        assertEquals("Category 1", result.getCategory());
        assertEquals(0, result.getQuestions().size());
    }
    @Test
    public void testUpdateQuiz()throws Exception {
        long quizId = 1L;
        QuizRequest quizRequest= QuizRequest.builder()
                .category("testCategory")
                .questions(new ArrayList<>())
                .build();
        Quiz originalQuiz = Quiz.builder()
                .id(quizId)
                .category("Original Category")
                .questions(new ArrayList<>())
                .build();
        QuizRequest updatedQuizRequest = new QuizRequest("Updated Category", null);
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(originalQuiz));
        when(quizRepository.save(originalQuiz)).thenReturn(originalQuiz);
        when(questionRepository.saveAll(new ArrayList<>())).thenReturn(new ArrayList<>());
        QuizService quizService = new QuizService(quizRepository, questionRepository);
        String result = quizService.updateQuiz(quizId, quizRequest);
        assertEquals("Successfully updated the quiz with ID", result);
        assertEquals("Updated Category", updatedQuizRequest.getCategory());
    }
    @Test
    public void testDeleteById()throws Exception {
        long quizId = 1L;
        Quiz quizToDelete = Quiz.builder()
                .id(quizId)
                .category("Category 1")
                .questions(new ArrayList<>())
                .build();
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quizToDelete));
        String result = quizService.deleteById(quizId);
        assertEquals("Quiz with ID " + quizId + " deleted successfully", result);
        verify(quizRepository).delete(quizToDelete);
    }
    @Test
    public void testGetQuizzesByCategory()throws Exception {
        String category = "Category1";
        List<Quiz> mockQuizzes = new ArrayList<>();
        Quiz quiz = Quiz.builder()
                .id(1L)
                .category(category)
                .questions(new ArrayList<>())
                .build();
        mockQuizzes.add(quiz);
        when(quizRepository.findByCategory(category)).thenReturn(mockQuizzes);
        List<QuizResponse> actualResponses = quizService.getQuizzesByCategory(category);
        List<QuizResponse> expectedResponses = new ArrayList<>();
        expectedResponses.add(QuizResponse.builder()
                .id(1L)
                .category(category)
                .questions(new ArrayList<>())
                .build());
        assertEquals(1, actualResponses.size());
    }
    @Test
    void testGetQuizzesByCategory_throwsException(){
    String category="Malayalam";
        List<Quiz> quizzes=new ArrayList<>();
        when(quizRepository.findByCategory(category)).thenReturn(quizzes);
        RuntimeException exception=assertThrows(RuntimeException.class, () -> {
            quizService.getQuizzesByCategory(category);
        });
        assertEquals("Quizzes not found",exception.getMessage());
    }
    @Test
    public void testAddQuestionToQuiz()throws Exception {
        long quizId = 1L;
        Quiz quiz = Quiz.builder()
                .id(quizId)
                .category("Category 1")
                .questions(new ArrayList<>())
                .build();
        QuestionRequest questionRequest = new QuestionRequest("New Question");
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(questionRepository.save(any(Question.class))).thenAnswer(invocation -> {
            Question question = invocation.getArgument(0);
            Question savedQuestion = Question.builder()
                    .id(2L)
                    .question(question.getQuestion())
                    .build();
            return savedQuestion;
        });
        QuizService quizService = new QuizService(quizRepository, questionRepository);
        String result = quizService.addQuestionToQuiz(quizId, questionRequest);
        assertEquals("Successfully added a question to the quiz with ID " + quizId, result);
        assertEquals(1, quiz.getQuestions().size());
        assertEquals("New Question", quiz.getQuestions().get(0).getQuestion());
    }
    @Test
    void testAddQuestionToQuiz_throwsException(){
    Long quizId=1L;
    QuestionRequest request=QuestionRequest.builder()
                    .question("Hello")
                            .build();
    when(quizRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException exception=assertThrows(EntityNotFoundException.class, () -> {
            quizService.addQuestionToQuiz(quizId, request);
        });
        assertEquals("Quiz not found with ID: "+quizId,exception.getMessage());
    }
    @Test
    public void testDeleteQuestionFromQuiz()throws Exception {
        long quizId = 1L;
        long questionId = 1L;
        Question question = Question.builder()
                .id(questionId)
                .question("Question 1")
                .build();
        Quiz quiz = Quiz.builder()
                .id(quizId)
                .category("Category 1")
                .questions(new ArrayList<>())
                .build();
        quiz.getQuestions().add(question);
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        QuizService quizService = new QuizService(quizRepository, questionRepository);
        String result = quizService.deleteQuestionFromQuiz(quizId, questionId);
        assertEquals("Successfully deleted the question with ID " + questionId + " from the quiz with ID " + quizId, result);
        assertEquals(0, quiz.getQuestions().size());
    }
    @Test
    public void testDeleteQuestionFromQuiz_throwsEntityNotFoundException() {
        Long quizId = 1L;
        Long questionId = 123L;
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());
      EntityNotFoundException exception=assertThrows(EntityNotFoundException.class, () -> {
          quizService.deleteQuestionFromQuiz(quizId, questionId);
      });
        assertEquals("Quiz not found with ID: " + quizId,exception.getMessage());
    }
    @Test
    void testFindQuestionById_throwsException(){
        Long quizId = 1L;
        Long questionId = 123L;
        Quiz quiz=Quiz.builder()
                .id(1L)
                .category("Jessin")
                .build();
        EntityNotFoundException exception=assertThrows(EntityNotFoundException.class, () -> {
            quizService.findQuestionById(quiz, questionId);
        });
        assertEquals("Question not found with ID: " + questionId,exception.getMessage());
    }
    @Test
    void testRemoveQuestionFromQuiz_throwsException(){
        Long quizId = 1L;
        Long questionId = 123L;
        Quiz quiz=Quiz.builder()
                .id(1L)
                .category("Jessin")
                .build();
        Question question=Question.builder()
                .id(questionId)
                .question("Test")
                .build();
        EntityNotFoundException exception=assertThrows(EntityNotFoundException.class, () -> {
            quizService.removeQuestionFromQuiz(quiz, question);
        });
        assertEquals("Questions not found in the quiz",exception.getMessage());
    }
        @Test
    void testUpdateQuestionInQuiz() {
        Long quizId = 1L;
        Long questionId = 1L;
        QuestionRequest updatedQuestionRequest = new QuestionRequest("Updated Question Text");
        Quiz quiz = Quiz.builder()
                .id(quizId)
                .questions(new ArrayList<>())
                .build();
Question question= Question.builder()
        .id(1L)
        .question("Test")
        .build();
quiz.getQuestions().add(question);
QuestionResponse response1= QuestionResponse.builder()
        .id(1L)
        .question("Updated Question Text")
        .build();
QuizResponse updated=QuizResponse.builder()
                .id(1L)
        .questions(new ArrayList<>())
                .build();
updated.getQuestions().add(response1);
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(questionRepository.save(any(Question.class))).thenAnswer(invocation -> invocation.getArgument(0));
        String response = quizService.updateQuestionInQuiz(quizId, questionId, updatedQuestionRequest);

        verify(quizRepository).findById(quizId);
        verify(questionRepository).save(any(Question.class));

        assertEquals("Successfully updated the question in the quiz", response);

        assertEquals("Updated Question Text", response1.getQuestion());
    }
        @Test
    public void testGetQuestionsForQuiz()throws Exception {
        long quizId = 1L;
        List<Question> questions = new ArrayList<>();
        questions.add(Question.builder().id(1L).question("Question 1").build());
        questions.add(Question.builder().id(2L).question("Question 2").build());
        Quiz quiz = Quiz.builder()
                .id(quizId)
                .category("Category 1")
                .questions(questions)
                .build();
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        QuizService quizService = new QuizService(quizRepository, null);
        List<QuestionResponse> result = quizService.getAllQuestionsForQuiz(quizId);
        assertEquals(2, result.size());
        assertEquals("Question 1", result.get(0).getQuestion());
        assertEquals("Question 2", result.get(1).getQuestion());
    }
  }
