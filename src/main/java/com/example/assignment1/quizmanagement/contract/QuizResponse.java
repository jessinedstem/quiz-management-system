package com.example.assignment1.quizmanagement.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizResponse {
    private long id;
    private String category;
    private List<QuestionResponse> questions;
}
