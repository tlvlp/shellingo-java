package com.tlvlp.shellingo;

import java.util.Objects;

public class VocabularyItem {

    private String id;
    private String question;
    private String solution;
    private String location;
    private int successCount;
    private int errorCount;

    public VocabularyItem incrementErrorCount() {
        errorCount++;
        return this;
    }

    public VocabularyItem incrementSuccessCount() {
        successCount++;
        return this;
    }

    @Override
    public String toString() {
        return "{\"Question\":{"
                + "\"id\":\"" + id + "\""
                + ", \"question\":\"" + question + "\""
                + ", \"solution\":\"" + solution + "\""
                + ", \"successCount\":\"" + successCount + "\""
                + ", \"errorCount\":\"" + errorCount + "\""
                + ", \"location\":\"" + location + "\""
                + "}}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VocabularyItem that = (VocabularyItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public VocabularyItem setId(String id) {
        this.id = id;
        return this;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public VocabularyItem setSuccessCount(int successCount) {
        this.successCount = successCount;
        return this;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public VocabularyItem setErrorCount(int errorCount) {
        this.errorCount = errorCount;
        return this;
    }

    public String getQuestion() {
        return question;
    }

    public VocabularyItem setQuestion(String question) {
        this.question = question;
        return this;
    }

    public String getSolution() {
        return solution;
    }

    public VocabularyItem setSolution(String solution) {
        this.solution = solution;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public VocabularyItem setLocation(String location) {
        this.location = location;
        return this;
    }
}
