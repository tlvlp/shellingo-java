package com.tlvlp.shellingo;

import lombok.val;

import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Shellingo {

    private static final Random rand = new Random();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Set<VocabularyItem> allVocabularyItems = new HashSet<>();
    private static final String DEFAULT_PATH = "./questions";

    public static void main(String[] args) {
        start(args);
    }

    public static void start(String[] args) {
        if (args.length > 1) {
            throw new RuntimeException("Too many arguments.");
        }
        val questionsParent = args.length != 0 ? args[0] : DEFAULT_PATH;

        allVocabularyItems.addAll(Parser.getVocabularyItems(questionsParent));

        boolean passed = true;
        var remainingQuestions = new HashSet<>(allVocabularyItems);
        VocabularyItem currentQuestion = null;

        System.out.printf(
                "Welcome to shellingo :)%n" +
                        "You have loaded %d questions. " +
                        "(type 'q' to quit)%n", remainingQuestions.size());

        while (true) {
            try {
                if (passed) {
                    remainingQuestions.remove(currentQuestion);
                    if (remainingQuestions.isEmpty()) {
                        remainingQuestions.addAll(allVocabularyItems);
                        System.out.println("Congrats, You have completed a cycle!");
                        printSummary();
                        System.out.println("Keep practicing or send 'q' to quit");
                    }
                    currentQuestion = selectNewQuestionOnSuccess(remainingQuestions);
                    passed = false;
                }
                var answer = postQuestion(currentQuestion);
                checkForExitRequest(answer);
                passed = checkAnswer(answer, currentQuestion);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                System.exit(1);
            }

        }
    }

    private static VocabularyItem selectNewQuestionOnSuccess(HashSet<VocabularyItem> remainingQuestions) {
        var randInt = rand.nextInt(remainingQuestions.size());
        int index = 0;
        for (VocabularyItem vi : remainingQuestions) {
            if (index == randInt) {
                return vi;
            }
            index++;
        }
        throw new RuntimeException("There was a problem with retrieving the next question");
    }

    private static void checkForExitRequest(String answer) {
        if (answer.equals("q")) {
            System.out.println("Thanks for practicing :) here is your summary: ");
            printSummary();
            System.out.println("Bye!");
            System.exit(0);
        }
    }

    private static String postQuestion(VocabularyItem vocabItem) {
        System.out.printf("%s: ", vocabItem.getQuestion());
        return scanner.nextLine();
    }

    private static boolean checkAnswer(String answer, VocabularyItem vocabItem) {
        var solution = vocabItem.getSolution();
        var isAnswerCorrect = prepareForComparison(solution).equals(prepareForComparison(answer));
        if (isAnswerCorrect) {
            System.out.println("Correct :)");
            allVocabularyItems.add(vocabItem.withIncrementedSuccessCount());
            return true;
        } else {
            System.err.println("Try again:");
            allVocabularyItems.add(vocabItem.withIncrementedErrorCount());
            return false;
        }
    }

    private static String prepareForComparison(String string) {
        return string
                .strip()
                .toLowerCase()
                .replaceAll("\\s{2,}", " ")
                .replaceAll("[?,!.]", "");
    }

    private static void printSummary() {
        System.out.println("====================");
        var successSummary = 0;
        var errorSummary = 0;
        for (VocabularyItem item : allVocabularyItems) {
            successSummary += item.getSuccessCount();
            errorSummary += item.getErrorCount();
        }
        System.out.println("Number of correct answers: " + successSummary);
        System.out.println("Number of mistakes: " + errorSummary);
        System.out.println("====================");
    }
}
