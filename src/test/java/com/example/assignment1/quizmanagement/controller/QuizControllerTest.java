package com.example.assignment1.quizmanagement.controller;

import com.example.assignment1.quizmanagement.contract.QuestionRequest;
import com.example.assignment1.quizmanagement.contract.QuestionResponse;
import com.example.assignment1.quizmanagement.contract.QuizRequest;
import com.example.assignment1.quizmanagement.contract.QuizResponse;
import com.example.assignment1.quizmanagement.service.QuizService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
public class QuizControllerTest {
    @Autowired
    private MockMvc mockMvc;
//    @InjectMocks
//    private QuizController quizController;
    @MockBean
    private QuizService quizService;

    @Test
    public void testGetAllQuizzes() throws Exception {
        QuizResponse quiz1 = QuizResponse.builder()
                .id(1L)
                .category("Category 1")
                .questions(new ArrayList<>())
                .build();
        QuizResponse quiz2 = QuizResponse.builder()
                .id(2L)
                .category("Category 2")
                .questions(new ArrayList<>())
                .build();
        List<QuizResponse> quizResponses = new ArrayList<>();
        quizResponses.add(quiz1);
        quizResponses.add(quiz2);
        when(quizService.findAllQuizzes()).thenReturn(quizResponses);
        mockMvc.perform(get("/quizzes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }
    @Test
    void testCreateQuiz() throws Exception {
        QuizRequest quizRequest = QuizRequest.builder()
                .category("Category 1")
                .questions(new ArrayList<>())
                .build();

        QuestionRequest questionRequest = QuestionRequest.builder()
                .question("Question 1")
                .build();

        List<QuestionRequest> questions = new ArrayList<>();
        questions.add(questionRequest);
        quizRequest.setQuestions(questions);

        QuizResponse expectedResponse = QuizResponse.builder()
                .id(1L)
                .category("Category 1")
                .questions(new ArrayList<>())
                .build();

        when(quizService.createQuiz(quizRequest)).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quizRequest)))
                .andExpect(status().isCreated());
    }
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
@Test
    public void testGetQuizById() throws Exception {
        long quizId = 1L;
        QuizResponse quizResponse = QuizResponse.builder()
                .id(quizId)
                .category("Category 1")
                .questions(new ArrayList<>())
                .build();
        when(quizService.getQuizById(quizId)).thenReturn(quizResponse);
        mockMvc.perform(get("/quizzes/{quizId}", quizId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUpdateQuiz() throws Exception {
        long quizId = 1L;
        QuizRequest updatedQuizRequest = new QuizRequest("Updated Category", null);
        when(quizService.updateQuiz(quizId, updatedQuizRequest)).thenReturn("Successfully updated the quiz with ID " + quizId);
        mockMvc.perform(put("/quizzes/{quizId}", quizId)
                        .content(new ObjectMapper().writeValueAsString(updatedQuizRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

          }
    @Test
    public void testDeleteQuizById() throws Exception {
        long quizId = 1L;
        when(quizService.deleteById(quizId)).thenReturn("Quiz with ID " + quizId + " deleted successfully");
        mockMvc.perform(delete("/quizzes/{quizId}",quizId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Quiz with ID " + quizId + " deleted successfully"));
        }
    @Test
    public void testGetQuizzesByCategory() throws Exception {
        String category = "Category1";
        List<QuizResponse> mockResponses = new ArrayList<>();
                      when(quizService.getQuizzesByCategory(category))
                .thenReturn(mockResponses);
        mockMvc.perform(MockMvcRequestBuilders.get("/quizzes/category/{category}", category)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
 }
    @Test
    public void testAddQuestionToQuiz() throws Exception {
        long quizId = 1L;
        QuestionRequest questionRequest = new QuestionRequest("New Question");
        when(quizService.addQuestionToQuiz(quizId, questionRequest))
                .thenReturn("Successfully added a question to the quiz with ID " + quizId);
        mockMvc.perform(post("/quizzes/{quizId}/questions", quizId)
                        .content(new ObjectMapper().writeValueAsString(questionRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
 }
    @Test
    public void testDeleteQuestionFromQuiz() throws Exception {
        long quizId = 1L;
        long questionId = 1L;
        when(quizService.deleteQuestionFromQuiz(quizId, questionId))
                .thenReturn("Successfully deleted the question with ID " + questionId + " from the quiz with ID " + quizId);
        mockMvc.perform(MockMvcRequestBuilders.delete("/quizzes/{quizId}/questions/{questionId}", quizId, questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    public void testUpdateQuestionInQuiz() throws Exception {
        long quizId = 1L;
        long questionId = 1L;
        String updatedQuestion = "Updated Question";

        QuestionRequest request = new QuestionRequest(updatedQuestion);
        String expectedResult = "Successfully updated the question in the quiz with ID " + quizId;

        when(quizService.updateQuestionInQuiz(quizId, questionId, request)).thenReturn(expectedResult);

        ResultActions resultActions = mockMvc.perform(put("/quizzes/{quizId}/questions/{questionId}", quizId, questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"question\": \"" + updatedQuestion + "\"}"));

        resultActions.andExpect(status().isOk());
    }
    @Test
    public void testGetQuestionsForQuiz() throws Exception {
        long quizId = 1L;
        List<QuestionResponse> questions = new ArrayList<>();
        questions.add(new QuestionResponse(1L, "Question 1"));
        questions.add(new QuestionResponse(2L, "Question 2"));
        when(quizService.getAllQuestionsForQuiz(quizId)).thenReturn(questions);
        mockMvc.perform(get("/quizzes/{quizId}/questions", quizId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
           }
}