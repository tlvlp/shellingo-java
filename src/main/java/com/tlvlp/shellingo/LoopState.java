package com.tlvlp.shellingo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class LoopState {
    private List<Question> allQuestions;
    private List<Integer> remainingQuestionRefs;
    private Question currentQuestion;
}
