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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }
    public List<QuizResponse> findAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        if (quizzes.isEmpty()) {
            throw new RuntimeException("Quizzes not found");
        }
        List<QuizResponse> response = quizzes.stream()
                .map(quiz -> QuizResponse.builder()
                        .id(quiz.getId())
                        .category(quiz.getCategory())
                        .questions(quiz.getQuestions().stream()
                                .map(q -> new QuestionResponse(q.getId(), q.getQuestion()))
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
        return response;
    }
    public QuizResponse createQuiz(QuizRequest quizRequest) {
        Quiz quiz = Quiz.builder()
                .category(quizRequest.getCategory())
                .questions(new ArrayList<>())
                .build();
        for (QuestionRequest questionRequest : quizRequest.getQuestions()) {
            boolean questionExistsInQuiz = quiz.getQuestions().stream()
                    .anyMatch(q -> q.getQuestion().equals(questionRequest.getQuestion()));
            if (!questionExistsInQuiz) {
                List<Question> matchingQuestions = questionRepository.findByQuestion(questionRequest.getQuestion());
                if (!matchingQuestions.isEmpty()) {
                    Question existingQuestion = matchingQuestions.get(0);
                    quiz.getQuestions().add(existingQuestion);
                } else {
                    Question question = Question.builder()
                            .question(questionRequest.getQuestion())
                            .build();
                    Question savedQuestion = questionRepository.save(question);
                    quiz.getQuestions().add(savedQuestion);
                }
            }
        }
        quiz = quizRepository.save(quiz);
        return QuizResponse.builder()
                .id(quiz.getId())
                .category(quiz.getCategory())
                .questions(quiz.getQuestions().stream()
                        .map(q -> new QuestionResponse(q.getId(), q.getQuestion()))
                        .collect(Collectors.toList()))
                .build();
    }
    public QuizResponse getQuizById(long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz with ID " + quizId + " not found"));
        return QuizResponse.builder()
                .id(quiz.getId())
                .category(quiz.getCategory())
                .questions(quiz.getQuestions().stream()
                        .map(q -> new QuestionResponse(q.getId(), q.getQuestion()))
                        .collect(Collectors.toList()))
                .build();
    }
    public String updateQuiz(long quizId, QuizRequest quizRequest) {
        Quiz existingQuiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + quizId));
        List<Question> addedQuestions = new ArrayList<>();
        if(quizRequest.getQuestions()!=null) {
            List<QuestionRequest> questions = quizRequest.getQuestions();
            for (QuestionRequest questionRequest : questions) {
                Question question = Question.builder()
                        .question(questionRequest.getQuestion())
                        .build();
                addedQuestions.add(question);
            }
        }
            questionRepository.saveAll(addedQuestions);
            existingQuiz.getQuestions().addAll(addedQuestions);
            Quiz savedQuiz=Quiz.builder().id(existingQuiz.getId())
                    .category(quizRequest.getCategory())
                    .questions(existingQuiz.getQuestions())
                    .build();
            Quiz updatedQuiz=quizRepository.save(savedQuiz);
        return "Successfully updated the quiz with ID";
    }
    public String deleteById(Long quizId) {
        Quiz existingQuiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz with ID " + quizId + " not found"));
        quizRepository.delete(existingQuiz);
        return "Quiz with ID " + quizId + " deleted successfully";
    }

    public List<QuizResponse> getQuizzesByCategory(String category) {
        List<Quiz> quizzes = quizRepository.findByCategory(category);
        if (quizzes.isEmpty()) {
            throw new RuntimeException("Quizzes not found");
        }
        List<QuizResponse> response = quizzes.stream()
                .map(quiz -> QuizResponse.builder()
                        .id(quiz.getId())
                        .category(quiz.getCategory())
                        .questions(quiz.getQuestions().stream()
                                .map(q -> new QuestionResponse(q.getId(), q.getQuestion()))
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
        return response;
    }
    public String addQuestionToQuiz(Long quizId, QuestionRequest questionRequest) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + quizId));
        Question newQuestion = Question.builder()
                                    .question(questionRequest.getQuestion())
                                     .build();
        Question addedQuestion=questionRepository.save(newQuestion);
        quiz.getQuestions().add(addedQuestion);
        quizRepository.save(quiz);
        return "Successfully added a question to the quiz with ID " + quizId;
    }
    public String deleteQuestionFromQuiz(Long quizId, Long questionId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + quizId));
        Question question = findQuestionById(quiz, questionId);
        removeQuestionFromQuiz(quiz, question);
        quizRepository.save(quiz);
        return "Successfully deleted the question with ID " + questionId + " from the quiz with ID " + quizId;
    }

    private Question findQuestionById(Quiz quiz, Long questionId) {
        if (quiz.getQuestions() == null) {
            throw new EntityNotFoundException("Question not found with ID: " + questionId);
        }
        return quiz.getQuestions().stream()
                .filter(q -> q.getId() == questionId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + questionId));
    }

    private void removeQuestionFromQuiz(Quiz quiz, Question question) {
        if (quiz.getQuestions() == null) {
            throw new EntityNotFoundException("Questions not found in the quiz");
        }
        quiz.getQuestions().remove(question);
    }

    public List<QuestionResponse> getAllQuestionsForQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + quizId));
        List<QuestionResponse> questions = quiz.getQuestions().stream()
                .map(question -> new QuestionResponse(question.getId(), question.getQuestion()))
                .collect(Collectors.toList());
        return questions;
    }

    public String updateQuestionInQuiz(Long quizId, Long questionId, QuestionRequest updatedQuestionRequest) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + quizId));
        Question questionToUpdate = findQuestionInQuiz(quiz, questionId);
        updateQuestionData(questionToUpdate, updatedQuestionRequest);
        quizRepository.save(quiz);
        return "Successfully updated the question in the quiz";
    }
    private Question findQuestionInQuiz(Quiz quiz, Long questionId) {
        if (quiz == null || quiz.getQuestions() == null) {
            throw new EntityNotFoundException("Quiz or Questions not found.");
        }
        return quiz.getQuestions()
                .stream()
                .filter(question -> question.getId() == questionId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + questionId));
    }
    private void updateQuestionData(Question question, QuestionRequest updatedQuestionRequest) {
        Question updatedQuestion = Question.builder()
                .id(question.getId())
                .question(updatedQuestionRequest.getQuestion())
                .build();
        questionRepository.save(updatedQuestion);
    }
    }