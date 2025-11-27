package com.vlad.todo.mapper;

import com.vlad.todo.dto.GroupTaskDtoResponse;
import com.vlad.todo.model.GroupTask;
import org.springframework.stereotype.Component;

@Component
public class GroupTaskMapper {

    public GroupTaskDtoResponse toDto(GroupTask task) {
        GroupTaskDtoResponse dto = new GroupTaskDtoResponse();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setContent(task.getContent());
        dto.setIsCompleted(task.getIsCompleted());
        dto.setIsImportant(task.getIsImportant());
        dto.setDeadlineDate(task.getDeadlineDate());

        if (task.getGroup() != null) {
            dto.setGroupId(task.getGroup().getId());
        }
        if (task.getAssignedUser() != null) {
            dto.setAssignedUserId(task.getAssignedUser().getId());
            dto.setAssignedUserName(task.getAssignedUser().getFirstName());
        }


        return dto;
    }
}
