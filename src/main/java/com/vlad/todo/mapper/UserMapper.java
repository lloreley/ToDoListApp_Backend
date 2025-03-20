package com.vlad.todo.mapper;

import com.vlad.todo.dto.TaskDtoRequest;
import com.vlad.todo.dto.TaskDtoResponse;
import com.vlad.todo.dto.UserDtoRequest;
import com.vlad.todo.dto.UserDtoResponse;
import com.vlad.todo.model.TaskEntity;
import com.vlad.todo.model.UserEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {
    private final TaskMapper taskMapper;

    public UserEntity toEntity(UserDtoRequest userDtoRequest) {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(userDtoRequest.getFirstName());
        userEntity.setLastName(userDtoRequest.getLastName());
        userEntity.setEmail(userDtoRequest.getEmail());
        userEntity.setPhone(userDtoRequest.getPhone());

        if (userDtoRequest.getTasks() != null) {
            List<TaskEntity> entityTasks = new ArrayList<>();
            for (TaskDtoRequest taskDtoRequest : userDtoRequest.getTasks()) {
                TaskEntity taskEntity = taskMapper.toEntity(taskDtoRequest);
                taskEntity.setUser(userEntity);
                entityTasks.add(taskEntity);
            }
            userEntity.setTasks(entityTasks);
        }
        return userEntity;
    }

    public UserDtoResponse toDto(UserEntity userEntity) {
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        userDtoResponse.setId(userEntity.getId());
        userDtoResponse.setEmail(userEntity.getEmail());
        userDtoResponse.setFirstName(userEntity.getFirstName());
        userDtoResponse.setLastName(userEntity.getLastName());
        userDtoResponse.setPhone(userEntity.getPhone());
        List<TaskDtoResponse> tasksDtoResponse = new ArrayList<>();
        for (TaskEntity taskEntity : userEntity.getTasks()) {
            tasksDtoResponse.add(taskMapper.toDto(taskEntity));
        }
        userDtoResponse.setTasks(tasksDtoResponse);
        return userDtoResponse;
    }
}
