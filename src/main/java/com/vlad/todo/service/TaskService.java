package com.vlad.todo.service;

import com.vlad.todo.dto.TaskDtoRequest;
import com.vlad.todo.dto.TaskDtoResponse;
import com.vlad.todo.exception.NotFoundException;
import com.vlad.todo.exception.UpdateException;
import com.vlad.todo.mapper.TaskMapper;
import com.vlad.todo.model.TaskEntity;
import com.vlad.todo.model.UserEntity;
import com.vlad.todo.repository.TaskRepository;
import com.vlad.todo.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskMapper taskMapper;
    private TaskRepository taskRepository;
    private UserRepository userRepository;

    public List<TaskDtoResponse> findAllTasks() {
        List<TaskDtoResponse> tasksDtoResponse = new ArrayList<>();
        taskRepository.findAll().forEach(
                taskEntity -> tasksDtoResponse.add(taskMapper.toDto(taskEntity)));
        return tasksDtoResponse;
    }

    public TaskDtoResponse findTaskById(long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Task with id %d not found", id)));
        return taskMapper.toDto(taskEntity);
    }

    public TaskDtoResponse saveTask(TaskDtoRequest taskDtoRequest) {
        UserEntity userEntity = userRepository.findById(taskDtoRequest.getUserId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id %d not found", taskDtoRequest.getUserId())));
        TaskEntity taskEntity = taskMapper.toEntity(taskDtoRequest);
        taskEntity.setUser(userEntity);
        taskRepository.save(taskEntity);
        return taskMapper.toDto(taskEntity);
    }

    public TaskDtoResponse updateTask(long id, TaskDtoRequest taskDtoRequest) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Task with id %d not found", id)));

        if (taskDtoRequest.getTitle() != null) {
            taskEntity.setTitle(taskDtoRequest.getTitle());
        }
        if (taskDtoRequest.getContent() != null) {
            taskEntity.setContent(taskDtoRequest.getContent());
        }
        if (taskDtoRequest.getIsCompleted() != null) {
            taskEntity.setIsCompleted(taskDtoRequest.getIsCompleted());
        }
        if (taskDtoRequest.getDeadlineDate() != null) {
            taskEntity.setDeadlineDate(taskDtoRequest.getDeadlineDate());
        }
        if (taskDtoRequest.getIsImportant() != null) {
            taskEntity.setIsImportant(taskDtoRequest.getIsImportant());
        }
        try {
            taskRepository.save(taskEntity);
            return taskMapper.toDto(taskEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new UpdateException("Error updating task with id: " + id);
        }
    }

    public void deleteTaskById(long id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException(
                String.format("Task with id %d not found.", id));
        }
        taskRepository.deleteById(id);
    }
}
