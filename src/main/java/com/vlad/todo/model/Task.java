package com.vlad.todo.model;

public class Task {
    private String content;
    private int id;
    private boolean isCompleted;

    public Task(String content, int id, boolean isCompleted) {
        this.content = content;
        this.id = id;
        this.isCompleted = isCompleted;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}
