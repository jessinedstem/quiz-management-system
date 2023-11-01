package com.example.assignment1.quizmanagement.contract;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class QuestionResponse {
    private long id;
    private String question;
}
