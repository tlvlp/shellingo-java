package com.tlvlp.shellingo;

import lombok.val;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Shellingo {

    private static final String DEFAULT_PATH = "./questions";
    private static final Random rand = new Random();
    private static final int CLUE_PENALTY = 5;
    private static final int REVEAL_PENALTY = 10;

    public static void main(String[] args) {
        start(args);
    }

    public static void start(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            val questionsAtStart = new ArrayList<>(Parser.getQuestions(getParentPathFrom(args)));
            LoopState state = new LoopState(
                    questionsAtStart,
                    getQuestionReferences(questionsAtStart),
                    null
            );

            System.out.printf("""
                            Welcome to shellingo :)
                            You have loaded %d questions.
                            """,
                    state.allQuestions().size());
            printHelpMessage();

            while (true) {
                try {
                    if (state.currentQuestion() == null) {
                        if (state.remainingQuestionRefs().isEmpty()) {
                            System.out.println("Congrats, You have completed a round!");
                            printSummary(state);
                            resetLoop(state);
                            printHelpMessage();
                        }
                        pickNewQuestionFrom(state);
                    }

                    var answer = postQuestion(state.currentQuestion(), scanner);

                    if (!answer.startsWith("/"))
                        continue;

                    switch (answer) {
                        case "/clue", "/c" -> revealCluesForPenalty(state);
                        case "/solution" -> revealSolutionsForPenalty(state);
                        case "/sum" -> printSummary(state);
                        case "/reset" -> {
                            System.out.println("Resetting round!");
                            resetLoop(state);
                        }
                        case "/h3" -> resetToHardest(3, state);
                        case "/h5" -> resetToHardest(5, state);
                        case "/h10" -> resetToHardest(10, state);
                        case "/quit", "/q" -> {
                            printSummary(state);
                            System.out.println("Quitting shellingo, have a nice day! :)");
                            System.exit(0);
                        }
                        default -> handleAttempt(answer, state);
                    }

                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                    System.exit(1);
                }
            }
        }
    }

    private static void handleAttempt(String answer, LoopState state) {
        val passed = checkAnswer(answer, state.currentQuestion());
        if (passed) {
            System.out.println("Correct!");
            state.currentQuestion().incrementCorrectCountBy(1);
            state.currentQuestion(null);
        } else {
            System.out.println("Try again:");
            state.currentQuestion().incrementErrorCountBy(1);
        }
    }

    private static void resetLoop(LoopState state) {
        // Questions remain the same
        resetLoopTo(state, state.allQuestions());
    }

    private static void resetLoopTo(LoopState state, List<Question> newQuestions) {
        state.allQuestions(newQuestions);
        state.allQuestions().forEach(Question::resetRound);
        state.remainingQuestionRefs(getQuestionReferences(state.allQuestions()));
        state.currentQuestion(null);
    }

    private static void resetToHardest(int numberOfQuestions, LoopState state) {
        System.out.printf("Resetting round to the hardest %d questions (with most overall error count)%n", numberOfQuestions);
        resetLoopTo(state, getHardestQuestions(numberOfQuestions, state));
    }

    private static boolean checkAnswer(String answerRaw, Question question) {
        val answer = prepareForComparison(answerRaw);
        return question.getSolutions()
                .stream()
                .map(Shellingo::prepareForComparison)
                .anyMatch(answer::equals);
    }

    private static String getParentPathFrom(String[] args) {
        if (args.length > 1) {
            throw new RuntimeException("Too many arguments.");
        }
        return args.length != 0 ? args[0] : DEFAULT_PATH;
    }

    private static List<Integer> getQuestionReferences(List<Question> allQuestions) {
        return IntStream.range(0, allQuestions.size()).boxed().collect(Collectors.toList());
    }

    private static void printHelpMessage() {
        System.out.println(
                """
                        Type
                             '/clue' or '/c' to print clues for the possible answers.
                             '/solution' to print the answer.
                             '/sum' to print a summary.
                             '/reset' to reset round.
                             '/h3' to practice the hardest 3 question (with most wrong answers)
                             '/h5' to practice the hardest 5 question (with most wrong answers)
                             '/h10' to practice the hardest 10 question (with most wrong answers)
                             '/quit' or '/q' to quit.
                        """);
    }

    private static void pickNewQuestionFrom(LoopState state) {
        var index = rand.nextInt(state.remainingQuestionRefs().size());
        var selectedRef = state.remainingQuestionRefs().get(index);
        state.remainingQuestionRefs().remove(index);
        state.currentQuestion(state.allQuestions().get(selectedRef));
    }

    private static String postQuestion(Question question, Scanner scanner) {
        System.out.printf("%s: ", question.getQuestion());
        return scanner.nextLine();
    }

    private static String prepareForComparison(String string) {
        return string
                .strip()
                .toLowerCase()
                .replaceAll("\\s{2,}", " ")
                .replaceAll("[?,!.:;]", "");
    }

    private static void printSummary(LoopState state) {
        System.out.println("====================");
        System.out.println("Questions (hardest first):");

        val summary = state.allQuestions()
                .stream()
                .sorted(hardestFirstComparator())
                .peek(question ->
                        System.out.printf("Wrong: %s [%s] Correct: %s [%s] Question: %s%n",
                                question.getErrorCountRound(),
                                question.getErrorCountSum(),
                                question.getCorrectCountRound(),
                                question.getCorrectCountSum(),
                                question.getQuestion()
                        )
                )
                .reduce(new Question()
                                .setCorrectCountRound(0)
                                .setCorrectCountSum(0)
                                .setErrorCountRound(0)
                                .setErrorCountSum(0),
                        Question::mergeCounts
                );

        System.out.println("====================");
        System.out.printf("Number of correct answers: %s [%s]%n", summary.getCorrectCountRound(), summary.getCorrectCountSum());
        System.out.printf("Number of wrong answers: %s [%s]%n", summary.getErrorCountRound(), summary.getErrorCountSum());
        System.out.println("====================");
    }

    private static Comparator<Question> hardestFirstComparator() {
        return Comparator.comparingInt(Question::getErrorCountSum).reversed();
    }


    private static void revealSolutionsForPenalty(LoopState state) {
        state.currentQuestion().incrementErrorCountBy(REVEAL_PENALTY);
        System.out.println("Solution(s): " + state.currentQuestion().getSolutions());
    }

    private static void revealCluesForPenalty(LoopState state) {
        state.currentQuestion().incrementErrorCountBy(CLUE_PENALTY);
        val maskedSolutions = state.currentQuestion().getSolutions()
                .stream()
                .map(solution -> IntStream.range(0, solution.length())
                        .mapToObj(index -> {
                            val c = solution.charAt(index);
                            if (index % 2 == 0) {
                                return c;
                            }
                            return c == ' ' ? ' ' : '*';
                        })
                        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                )
                .toList();

        System.out.println("Clue(s): " + maskedSolutions);
    }

    private static List<Question> getHardestQuestions(int maxCount, LoopState state) {
        return state.allQuestions()
                .stream()
                .sorted(hardestFirstComparator())
                .limit(maxCount)
                .collect(Collectors.toList());
    }
}
