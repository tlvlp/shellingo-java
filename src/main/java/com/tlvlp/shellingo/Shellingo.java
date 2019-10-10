package com.tlvlp.shellingo;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Shellingo implements Runnable{

    private Set<VocabularyItem> vocabularyItems;
    private Random rand;
    private Scanner scanner;

    public Shellingo() {
        try {
            vocabularyItems = new Parser().getVocabualryItems();
            rand = new Random();
            scanner = new Scanner(System.in);
        } catch (IOException | NoQuestionsFoundException e) {
            System.err.println("Error in reading the question file(s): " + e.getMessage());
            System.exit(1);
        }

    }

    @Override
    public void run() {
        System.out.printf("Welcome to shellingo :)%n" +
                "(type 'q' to exit)%n");

        boolean passed = true;
        VocabularyItem vocabItem = null;

        while (true) {
            try {
                if (passed) {
                    vocabItem = selectNewQuestionOnSuccess();
                    passed = false;
                }
                var answer = postQuestion(vocabItem);
                checkForExitRequest(answer);
                passed = checkAnswer(answer, vocabItem);
            } catch (NoQuestionsFoundException e) {
                System.err.println("Error: " + e.getMessage());
                System.exit(1);
            }

        }
    }

    private VocabularyItem selectNewQuestionOnSuccess() throws NoQuestionsFoundException {
        var randInt = rand.nextInt(vocabularyItems.size());
        int index = 0;
        for (VocabularyItem vi : vocabularyItems) {
            if (index == randInt) {
                return vi;
            }
            index++;
        }
        throw new NoQuestionsFoundException("There was a problem with retrieving the next question");
    }

    private void checkForExitRequest(String answer) {
        if (answer.equals("q")) {
            printPracticeSummary();
            System.out.println("Bye!");
            System.exit(0);
        }
    }

    private String postQuestion(VocabularyItem vocabItem) {
        System.out.printf("%s: ", vocabItem.getQuestion());
        return scanner.nextLine();
    }

    private boolean checkAnswer(String answer, VocabularyItem vocabItem) {
        var solution = vocabItem.getSolution();
        var result = solution.toLowerCase()
                .equals(answer.toLowerCase().strip());
        if (result) {
            System.out.println("Correct!");
            vocabularyItems.add(vocabItem.incrementSuccessCount());
            return true;
        } else {
            System.err.println("Not correct!");
            vocabularyItems.add(vocabItem.incrementErrorCount());
            return false;
        }
    }

    private void printPracticeSummary() {
        System.out.printf("====================%n" +
                "Practice summary:%n");
        var successSummary = 0;
        var errorSummary = 0;
        for (VocabularyItem item : vocabularyItems) {
            successSummary += item.getSuccessCount();
            errorSummary += item.getErrorCount();
        }
        System.out.println("Number of correct answers: " + successSummary);
        System.out.println("Number of mistakes: " + errorSummary);
    }
}
