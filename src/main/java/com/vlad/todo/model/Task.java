package com.vlad.todo.model;

import java.time.LocalDate;

public class Task {
    private String content;

    public Task(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
