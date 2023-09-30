package com.tlvlp.shellingo;

import lombok.val;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Shellingo {

    private static final String DEFAULT_PATH = "./questions";
    private static final Random rand = new Random();

    public static void main(String[] args) {
        start(args);
    }

    public static void start(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            val questionsParentPath = parseProgramArgs(args);
            List<Question> allQuestions = new ArrayList<>(Parser.getQuestions(questionsParentPath));
            List<Integer> remainingQuestionRefs = getQuestionReferences(allQuestions);
            Question currentQuestion = null;

            System.out.printf("""
                            Welcome to shellingo :)
                            You have loaded %d questions.
                            """,
                    allQuestions.size());
            printHelpMessage();

            while (true) {
                try {
                    if (currentQuestion == null) {
                        if (remainingQuestionRefs.isEmpty()) {
                            System.out.println("Congrats, You have completed a round!");
                            printSummary(allQuestions);
                            remainingQuestionRefs = getQuestionReferences(allQuestions);
                            allQuestions.forEach(Question::resetRound);
                            printHelpMessage();
                        }
                        currentQuestion = pickNewQuestionFrom(remainingQuestionRefs, allQuestions);
                    }

                    var answer = postQuestion(currentQuestion, scanner);

                    switch (answer) {
                        case "-c" -> printClue(currentQuestion);
                        case "-solution" -> System.out.println("The solution is: " + currentQuestion.getSolutions());
                        case "-s" -> printSummary(allQuestions);
                        case "-r" -> {
                            System.out.println("Resetting round!");
                            allQuestions.forEach(Question::resetRound);
                            remainingQuestionRefs = getQuestionReferences(allQuestions);
                            currentQuestion = null;
                        }
                        case "-h5" -> {
                            System.out.println("Resetting round to the hardest 5 questions (with most overall error count)");
                            allQuestions = getHardestQuestions(5, allQuestions);
                            remainingQuestionRefs = getQuestionReferences(allQuestions);
                            currentQuestion = null;
                        }
                        case "-q" -> {
                            printSummary(allQuestions);
                            System.out.println("Quitting shellingo, have a nice day! :)");
                            System.exit(0);
                        }
                        default -> {
                            // Evaluate the input as a response attempt
                            val passed = checkAnswer(answer, currentQuestion);
                            if (passed) {
                                System.out.println("Correct!");
                                currentQuestion.incrementCorrectCount();
                                currentQuestion = null;
                            } else {
                                System.out.println("Try again:");
                                currentQuestion.incrementErrorCount();
                            }
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                    System.exit(1);
                }
            }
        }
    }

    private static boolean checkAnswer(String answerRaw, Question question) {
        val answer = prepareForComparison(answerRaw);
        return question.getSolutions()
                .stream()
                .map(Shellingo::prepareForComparison)
                .anyMatch(answer::equals);
    }

    private static String parseProgramArgs(String[] args) {
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
                             '-c' to print a clue for the answer.
                             '-solution' to print the answer.
                             '-s' to print a summary.
                             '-r' to reset round.
                             '-h5' to practice the hardest 5 question (with most wrong answers)
                             '-q' to quit.
                        """);
    }

    private static Question pickNewQuestionFrom(List<Integer> remainingQuestionRefs, List<Question> allQuestions) {
        var index = rand.nextInt(remainingQuestionRefs.size());
        var selectedRef = remainingQuestionRefs.get(index);
        remainingQuestionRefs.remove(index);
        return allQuestions.get(selectedRef);
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

    private static void printSummary(List<Question> allQuestions) {
        System.out.println("====================");
        System.out.println("Questions (hardest first):");

        val summary = allQuestions.stream()
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


    private static void printClue(Question question) {
        val maskedSolutions = question.getSolutions()
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

        System.out.println("Clue: " + maskedSolutions);
    }

    private static List<Question> getHardestQuestions(int maxCount, List<Question> allQuestions) {
        return allQuestions.stream()
                .sorted(hardestFirstComparator())
                .limit(maxCount)
                .collect(Collectors.toList());
    }
}
