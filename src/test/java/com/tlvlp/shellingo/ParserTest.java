package com.tlvlp.shellingo;

import org.assertj.core.util.Files;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class ParserTest {

    @Test
    public void testAllScenarios() {
        // given we have the test files in the resources folder

        // when
        Set<VocabularyItem> items = Parser.getVocabularyItems("src/test/resources/questions");

        // then
        assertThat(items)
                .hasSize(3)
                .anyMatch(item -> item.getQuestion().equals("simple question") && item.getSolution().equals("simple answer"))
                .anyMatch(item -> item.getQuestion().equals("subfolder question") && item.getSolution().equals("subfolder answer"))
                .anyMatch(item -> item.getQuestion().equals("malformed question") && item.getSolution().equals("malformed answer"))
        ;
    }


}
