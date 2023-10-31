package com.example.assignment1.quizmanagement.controller;

import com.example.assignment1.quizmanagement.contract.QuestionRequest;
import com.example.assignment1.quizmanagement.contract.QuestionResponse;
import com.example.assignment1.quizmanagement.contract.QuizRequest;
import com.example.assignment1.quizmanagement.contract.QuizResponse;
import com.example.assignment1.quizmanagement.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
@RestController
@RequestMapping("/quizzes")
public class QuizController {
    private final QuizService quizService;
    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }
    @GetMapping()
    public ResponseEntity<List<QuizResponse>> getAllQuizzes() {
        List<QuizResponse> quizzes = quizService.findAllQuizzes();
        return ResponseEntity.ok(quizzes);
    }
    @PostMapping()
    public ResponseEntity<QuizResponse> createQuiz(@RequestBody QuizRequest quizRequest) {
        QuizResponse response = quizService.createQuiz(quizRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable long quizId) {
        return new ResponseEntity<>(quizService.getQuizById(quizId), HttpStatus.OK);
    }
    @PutMapping("/{quizId}")
    public ResponseEntity<String> updateQuiz(@PathVariable long quizId, @RequestBody QuizRequest quizRequest) {
        String updated = quizService.updateQuiz(quizId, quizRequest);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{quizId}")
    public ResponseEntity<String> deleteQuizById(@PathVariable Long quizId) {
        String response = quizService.deleteById(quizId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/category/{category}")
    public ResponseEntity<List<QuizResponse>> getQuizzesByCategory(@PathVariable String category) {
        List<QuizResponse> response = quizService.getQuizzesByCategory(category);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{quizId}/questions")
    public ResponseEntity<String> addQuestionToQuiz(
            @PathVariable Long quizId,
            @RequestBody QuestionRequest questionRequest) {
        String response = quizService.addQuestionToQuiz(quizId, questionRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @DeleteMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<String> deleteQuestionFromQuiz(@PathVariable Long quizId, @PathVariable Long questionId) {
        String response = quizService.deleteQuestionFromQuiz(quizId, questionId);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<String> updateQuestionInQuiz(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @RequestBody QuestionRequest updatedQuestionRequest) {
        String result = quizService.updateQuestionInQuiz(quizId, questionId, updatedQuestionRequest);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{quizId}/questions")
    public ResponseEntity<List<QuestionResponse>> getAllQuestionsForQuiz(@PathVariable Long quizId) {
        List<QuestionResponse> questions = quizService.getAllQuestionsForQuiz(quizId);
        return ResponseEntity.ok(questions);
    }

}
