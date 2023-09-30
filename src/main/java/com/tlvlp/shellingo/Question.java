package com.tlvlp.shellingo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Question {

    @EqualsAndHashCode.Include
    private String question;
    private Set<String> solutions;
    private String location;
    private int correctCountRound;
    private int errorCountRound;
    private int correctCountSum;
    private int errorCountSum;

    public void incrementErrorCountBy(int increment) {
        errorCountRound += increment;
        errorCountSum += increment;
    }

    public void incrementCorrectCountBy(int increment) {
        correctCountRound += increment;
        correctCountSum += increment;
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
