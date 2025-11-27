package com.vlad.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupTaskDtoRequest {

    @NotBlank(message = "Название задачи не может быть пустым")
    private String title;

    private String content;

    @NotNull(message = "Статус завершенности обязателен")
    private Boolean isCompleted = false;

    @NotNull(message = "Статус важности обязателен")
    private Boolean isImportant = false;

    @NotNull(message = "Дата дедлайна обязателен")
    private LocalDate deadlineDate;

    @NotNull(message = "ID группы обязателен")
    private Long groupId;

    private Long assignedUserId;
}
