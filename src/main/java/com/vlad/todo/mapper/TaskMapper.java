package com.vlad.todo.mapper;

import com.vlad.todo.dto.TaskDto;
import com.vlad.todo.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskDto toTaskDto(Task task) {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle(task.getTitle());
        taskDto.setContent(task.getContent());
        taskDto.setTaskDeadline(task.getTaskDeadline());
        taskDto.setIsImportant(task.getIsImportant());
        taskDto.setIsCompleted(task.getIsCompleted());
        return taskDto;
    }

    public Task fromTaskDto(TaskDto taskDto) {
        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setContent(taskDto.getContent());
        task.setTaskDeadline(taskDto.getTaskDeadline());
        task.setIsImportant(taskDto.getIsImportant());
        task.setIsCompleted(taskDto.getIsCompleted());
        return task;
    }

}
