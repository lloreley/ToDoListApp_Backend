package com.vlad.todo.mapper;

import com.vlad.todo.dto.TaskDtoRequest;
import com.vlad.todo.dto.TaskDtoResponse;
import com.vlad.todo.model.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskDtoResponse toDto(TaskEntity taskEntity) {
        TaskDtoResponse taskDtoResponse = new TaskDtoResponse();
        taskDtoResponse.setId(taskEntity.getId());
        taskDtoResponse.setTitle(taskEntity.getTitle());
        taskDtoResponse.setContent(taskEntity.getContent());
        taskDtoResponse.setDeadlineDate(taskEntity.getDeadlineDate());
        taskDtoResponse.setIsImportant(taskEntity.getIsImportant());
        taskDtoResponse.setIsCompleted(taskEntity.getIsCompleted());
        taskDtoResponse.setUserId(taskEntity.getUser().getId());
        return taskDtoResponse;
    }

    public TaskEntity toEntity(TaskDtoRequest taskDtoRequest) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTitle(taskDtoRequest.getTitle());
        taskEntity.setContent(taskDtoRequest.getContent());
        taskEntity.setDeadlineDate(taskDtoRequest.getDeadlineDate());
        taskEntity.setIsImportant(taskDtoRequest.getIsImportant());
        taskEntity.setIsCompleted(taskDtoRequest.getIsCompleted());
        return taskEntity;
    }

}
