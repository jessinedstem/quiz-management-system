package com.example.assignment1.quizmanagement.contract;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRequest {
    private String category;
    private List<QuestionRequest> questions;
}
