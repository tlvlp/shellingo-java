package com.tlvlp.shellingo;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Parser {

    @SneakyThrows
    public static Set<Question> getQuestions(String questionsPath) {
        var questions = new HashSet<Question>();
        var parsingErrors = new ArrayList<String>();
        Files.walkFileTree(Paths.get(questionsPath), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile()) {
                    try (var lines = Files.lines(file)) {
                        lines.filter(line -> !line.isBlank())
                                .filter(line -> !line.startsWith("#"))
                                .map(line -> line.split("\\|"))
                                .forEach(lineArray -> {
                                    // Create or update questions
                                    if (lineArray.length == 2) {
                                        var questionStr = removeExtraWhitespaces(lineArray[0]);
                                        var solutionStr = removeExtraWhitespaces(lineArray[1]);
                                        final var question = questions.stream()
                                                .filter(q -> q.getQuestion().equals(questionStr))
                                                .findAny()
                                                .orElse(new Question()
                                                        .setQuestion(questionStr)
                                                        .setCorrectCountRound(0)
                                                        .setErrorCountRound(0)
                                                        .setCorrectCountSum(0)
                                                        .setErrorCountSum(0)
                                                        .setSolutions(new HashSet<>())
                                                        .setLocation(file.toString())
                                                );
                                        question.getSolutions().add(solutionStr);
                                        questions.add(question);
                                    } else {
                                        parsingErrors.add(String.format("File: %s   line: '%s'", file, lineArray[0]));
                                    }
                                });
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });

        if (questions.isEmpty()) {
            throw new RuntimeException("No questions found in the questions directory");
        }

        if (!parsingErrors.isEmpty()) {
            System.err.println("Errors found during parsing the questions. The following lines will be ignored:");
            parsingErrors.forEach(System.err::println);
        }
        return questions;
    }

    private static String removeExtraWhitespaces(String input) {
        return input.trim().replaceAll("\\s{2,}", " ");
    }

}
