package com.vlad.todo.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDtoRequest {
    protected String title;
    protected String content;
    protected Boolean isCompleted;
    protected LocalDate deadlineDate;
    protected Boolean isImportant;
    protected Long userId;
}
