package com.tlvlp.shellingo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Shellingo implements Runnable{

    private Set<VocabularyItem> allVocabularyItems;
    private Random rand;
    private Scanner scanner;

    public Shellingo() {
        try {
            allVocabularyItems = new Parser().getVocabualryItems();
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
        var remainingQuestions = new HashSet<>(allVocabularyItems);
        VocabularyItem currentQuestion = null;

        while (true) {
            try {
                if (passed) {
                    remainingQuestions.remove(currentQuestion);
                    if (remainingQuestions.isEmpty()) {
                        remainingQuestions.addAll(allVocabularyItems);
                    }
                    currentQuestion = selectNewQuestionOnSuccess(remainingQuestions);
                    passed = false;
                }
                var answer = postQuestion(currentQuestion);
                checkForExitRequest(answer);
                passed = checkAnswer(answer, currentQuestion);
            } catch (NoQuestionsFoundException e) {
                System.err.println("Error: " + e.getMessage());
                System.exit(1);
            }

        }
    }

    private VocabularyItem selectNewQuestionOnSuccess(HashSet<VocabularyItem> remainingQuestions) throws NoQuestionsFoundException {
        var randInt = rand.nextInt(remainingQuestions.size());
        int index = 0;
        for (VocabularyItem vi : remainingQuestions) {
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
        var isAnswerCorrect = cleanString(solution).equals(cleanString(answer));
        if (isAnswerCorrect) {
            System.out.println("Correct!");
            allVocabularyItems.add(vocabItem.withIncrementedSuccessCount());
            return true;
        } else {
            System.err.println("Not correct!");
            allVocabularyItems.add(vocabItem.withIncrementedErrorCount());
            return false;
        }
    }

    private String cleanString(String string) {
        return string
                .strip()
                .toLowerCase()
                .replaceAll(" +", " ")
                .replace("?", "")
                .replace(".", "")
                .replace(",", "")
                .replace("!", "");
    }

    private void printPracticeSummary() {
        System.out.printf("====================%n" +
                "Practice summary:%n");
        var successSummary = 0;
        var errorSummary = 0;
        for (VocabularyItem item : allVocabularyItems) {
            successSummary += item.getSuccessCount();
            errorSummary += item.getErrorCount();
        }
        System.out.println("Number of correct answers: " + successSummary);
        System.out.println("Number of mistakes: " + errorSummary);
    }
}
