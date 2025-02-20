package com.vlad.todo.model;

import java.time.LocalDate;

public class Task {
    private String Content;
    private LocalDate DateOfCreation;
    private LocalDate DateOfCompletion;

    public Task(String content, LocalDate dateOfCreation) {
        this.Content = content;
        this.DateOfCreation = dateOfCreation;
    }

}
