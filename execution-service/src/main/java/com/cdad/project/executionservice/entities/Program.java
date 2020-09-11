package com.cdad.project.executionservice.entities;

public class Program {
    String sourceCode;
    String input;
    Language language;
    Integer timeout=5;
    public Program() {
    }

    public Program(String sourceCode, String input,Language language) {
        this.sourceCode = sourceCode;
        this.input = input;
        this.language=language;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}