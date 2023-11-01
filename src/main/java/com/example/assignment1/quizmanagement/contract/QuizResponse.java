package com.example.assignment1.quizmanagement.contract;

import lombok.*;

import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizResponse {
    private long id;
    private String category;
    private List<QuestionResponse> questions;
}
