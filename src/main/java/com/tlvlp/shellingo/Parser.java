package com.tlvlp.shellingo;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class Parser {

    public Map<String, String> getQuestions() throws IOException, NoQuestionsFoundException {
        var questions = new HashMap<String, String>();
        Files.walkFileTree(Paths.get("./questions"), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if(attrs.isRegularFile()) {
                    Files.lines(file)
                            .filter(line -> !line.startsWith("#"))
                            .map(line -> line.split("\\|"))
                            .forEach(lineArray -> {
                                var question = lineArray[0].strip().replaceAll(" +", " ");
                                var answer = lineArray[1].strip().replaceAll(" +", " ");
                                questions.put(question, answer);
                            });
                }
                return FileVisitResult.CONTINUE;
            }
        });
        if (questions.isEmpty()) {
            throw new NoQuestionsFoundException("No questions found in the questions directory");
        }
        return questions;
    }


}
