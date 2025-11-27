package com.vlad.todo.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupTaskDtoResponse {

    private Long id;

    private String title;

    private String content;

    private Boolean isCompleted;

    private Boolean isImportant;

    private LocalDate deadlineDate;

    private Long groupId;

    private Long assignedUserId;

    private String assignedUserName;
}
