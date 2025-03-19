package com.vlad.todo.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class TaskDto {
    private String title;
    private String content;
    private Boolean isCompleted;
    private LocalDate taskDeadline;
    private Boolean isImportant;
}
