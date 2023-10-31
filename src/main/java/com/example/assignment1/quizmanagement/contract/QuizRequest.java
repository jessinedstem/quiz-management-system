package com.example.assignment1.quizmanagement.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRequest {
    private String category;
    private List<QuestionRequest> questions;
}
