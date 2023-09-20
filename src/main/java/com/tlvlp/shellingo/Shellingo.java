package com.tlvlp.shellingo;

import lombok.val;

import java.util.*;

public class Shellingo {

    private static final Random rand = new Random();
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<VocabularyItem> allVocabularyItems = new ArrayList<>();
    private static final String DEFAULT_PATH = "./questions";

    public static void main(String[] args) {
        start(args);
    }

    public static void start(String[] args) {
        if (args.length > 1) {
            throw new RuntimeException("Too many arguments.");
        }
        val questionsParentPath = args.length != 0 ? args[0] : DEFAULT_PATH;

        allVocabularyItems.addAll(Parser.getVocabularyItems(questionsParentPath));

        var remainingQuestions = new ArrayList<>(allVocabularyItems);
        VocabularyItem currentQuestion = null;

        System.out.printf(
                "Welcome to shellingo :)%n" +
                        "You have loaded %d questions. " +
                        "(type 'q' to quit)%n", remainingQuestions.size());

        while (true) {
            try {
                if(currentQuestion == null) {
                    if (remainingQuestions.isEmpty()) {
                        remainingQuestions.addAll(allVocabularyItems);
                        System.out.println("Congrats, You have completed a cycle!");
                        printSummary();
                        System.out.println("Keep practicing or type 'q' to quit");
                    }
                    currentQuestion = pickNewQuestionFrom(remainingQuestions);
                }

                var answer = postQuestion(currentQuestion);

                checkForExitRequest(answer);
                var passed = validateAnswer(answer, currentQuestion);
                if (passed) {
                    System.out.println("Correct :)");
                    currentQuestion.incrementedSuccessCount();
                    currentQuestion = null;
                } else {
                    System.err.println("Try again:");
                    currentQuestion.incrementedErrorCount();
                }

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                System.exit(1);
            }

        }
    }

    private static VocabularyItem pickNewQuestionFrom(List<VocabularyItem> remainingQuestions) {
        var index = rand.nextInt(remainingQuestions.size());
        var selected = remainingQuestions.get(index);
        remainingQuestions.remove(index);
        return selected;
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

    private static boolean validateAnswer(String answer, VocabularyItem vocabItem) {
        return prepareForComparison(vocabItem.getSolution()).equals(prepareForComparison(answer));
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
        var successRound = 0;
        var errorRound = 0;
        var successSummary = 0;
        var errorSummary = 0;
        for (VocabularyItem item : allVocabularyItems) {
            successRound += item.getSuccessCountRound();
            errorRound += item.getErrorCountRound();

            successSummary += item.getSuccessCountSum();
            errorSummary += item.getErrorCountSum();

            item.resetRound();
        }
        System.out.printf("Number of correct answers: %s [%s]%n", successRound, successSummary);
        System.out.printf("Number of mistakes: %s [%s]%n", errorRound, errorSummary);
        System.out.println("====================");
    }
}
