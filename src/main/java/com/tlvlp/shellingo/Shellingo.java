package com.tlvlp.shellingo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Shellingo implements Runnable{

    private Boolean passed;
    private Map<String, String> questions;
    private String question;
    private String solution;
    private Random rand;
    private Scanner scanner;

    public Shellingo() {

        try {
            passed = true;
            questions = new Parser().getQuestions();
            rand = new Random();
            scanner = new Scanner(System.in);
            question = "";
            solution = "";
        } catch (IOException | NoQuestionsFoundException e) {
            System.out.println("Error in reading the question file(s): " + e.getMessage());
            System.exit(1);
        }

    }

    @Override
    public void run() {
        System.out.printf("Welcome to shellingo :)%n(type 'q' to exit)%n");
        while (true) {
            selectNewQuestionOnSuccess();
            var answer = postQuestion(question);
            checkForExitRequest(answer);
            checkAnswer(solution, answer);
        }
    }

    private void selectNewQuestionOnSuccess() {
        if (passed) {
            question = chooseRandomQueston();
            solution = questions.get(question);
            passed = false;
        }
    }

    private String chooseRandomQueston() {
        var randInt = rand.nextInt(questions.size());
        var keys = new ArrayList<>(questions.keySet());
        return keys.get(randInt);
    }

    private void checkForExitRequest(String answer) {
        if (answer.equals("q")) {
            System.out.println("Bye!");
            System.exit(0);
        }
    }

    private String postQuestion(String question) {
        System.out.printf("%s: ", question);
        return scanner.nextLine();
    }

    private void checkAnswer(String solution, String answer) {
        var result = solution.toLowerCase()
                .equals(answer.toLowerCase().strip());
        if (result) {
            System.out.println("Correct!");
            passed = true;
        } else {
            System.out.println("Not correct!");
        }
    }
}
