package com.tlvlp.shellingo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
public class VocabularyItem {

    @EqualsAndHashCode.Include
    private String id;
    private String question;
    private String solution;
    private String location;
    private int successCountRound;
    private int errorCountRound;
    private int successCountSum;
    private int errorCountSum;

    public void incrementedErrorCount() {
        errorCountRound++;
        errorCountSum++;
    }

    public void incrementedSuccessCount() {
        successCountRound++;
        successCountSum++;
    }

    public void resetRound() {
        successCountRound = 0;
        errorCountRound = 0;
    }
}
