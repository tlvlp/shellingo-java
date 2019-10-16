package com.tlvlp.shellingo;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Parser {

    public Set<VocabularyItem> getVocabualryItems() throws IOException, NoQuestionsFoundException {
        var vocabItems = new HashSet<VocabularyItem>();
        var parsingErrors = new ArrayList<String>();
        Files.walkFileTree(Paths.get("./questions"), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if(attrs.isRegularFile()) {
                    Files.lines(file)
                            .filter(line -> !line.isBlank())
                            .filter(line -> !line.startsWith("#"))
                            .map(line -> line.split("\\|"))
                            .forEach(lineArray -> {
                                if (lineArray.length == 2) {
                                    var question = lineArray[0];
                                    var answer = lineArray[1];
                                    vocabItems.add(new VocabularyItem()
                                            .setId(LocalDateTime.now().toString())
                                            .setQuestion(question)
                                            .setSolution(answer)
                                            .setSuccessCount(0)
                                            .setErrorCount(0)
                                            .setLocation(file.toString())
                                    );
                                } else {
                                    parsingErrors.add(String.format("File: %s   line: '%s'", file, lineArray[0]));
                                }
                            });
                }
                return FileVisitResult.CONTINUE;
            }
        });
        if (vocabItems.isEmpty()) {
            throw new NoQuestionsFoundException("No questions found in the questions directory");
        }

        if (!parsingErrors.isEmpty()) {
            System.err.println("Errors found during parsing the questions. The following lines will be ignored:");
            parsingErrors.forEach(System.err::println);
        }
        return vocabItems;
    }


}
