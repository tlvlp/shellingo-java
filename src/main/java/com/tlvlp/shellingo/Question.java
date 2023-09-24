package com.tlvlp.shellingo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
public class Question {

    @EqualsAndHashCode.Include
    private String id;
    private String question;
    private String solution;
    private String location;
    private int correctCountRound;
    private int errorCountRound;
    private int correctCountSum;
    private int errorCountSum;

    public void incrementErrorCount() {
        errorCountRound++;
        errorCountSum++;
    }

    public void incrementCorrectCount() {
        correctCountRound++;
        correctCountSum++;
    }

    public void resetRound() {
        correctCountRound = 0;
        errorCountRound = 0;
    }

    public Question mergeCounts(Question other) {
        correctCountRound += other.getCorrectCountRound();
        correctCountSum += other.getCorrectCountSum();
        errorCountRound += other.getErrorCountRound();
        errorCountSum += other.getErrorCountSum();
        return this;
    }
}
