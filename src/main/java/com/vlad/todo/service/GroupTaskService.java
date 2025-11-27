package com.vlad.todo.service;

import com.vlad.todo.dto.GroupTaskDtoRequest;
import com.vlad.todo.dto.GroupTaskDtoResponse;
import com.vlad.todo.exception.NotFoundException;
import com.vlad.todo.mapper.GroupTaskMapper;
import com.vlad.todo.model.Group;
import com.vlad.todo.model.GroupTask;
import com.vlad.todo.model.Role;
import com.vlad.todo.model.User;
import com.vlad.todo.repository.GroupRepository;
import com.vlad.todo.repository.GroupTaskRepository;
import com.vlad.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupTaskService {

    private final GroupTaskRepository groupTaskRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupTaskMapper mapper;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : principal.toString();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Текущий пользователь не найден"));
    }

    private boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    public GroupTaskDtoResponse create(GroupTaskDtoRequest dto) {
        Group group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new NotFoundException("Группа не найдена"));

        User currentUser = getCurrentUser();

        // Пользователь должен быть в группе или быть админом
        if (!group.getUsers().contains(currentUser) && !isAdmin(currentUser)) {
            throw new NotFoundException("Вы не являетесь участником этой группы");
        }

        User assignedUser = null;
        if (dto.getAssignedUserId() != null && dto.getAssignedUserId() > 0) {
            assignedUser = userRepository.findById(dto.getAssignedUserId())
                    .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

            if (!group.getUsers().contains(assignedUser)) {
                throw new NotFoundException("Пользователь не состоит в этой группе");
            }
        }

        GroupTask task = new GroupTask();
        task.setTitle(dto.getTitle());
        task.setContent(dto.getContent());
        task.setIsCompleted(dto.getIsCompleted() != null && dto.getIsCompleted());
        task.setIsImportant(dto.getIsImportant() != null && dto.getIsImportant());
        task.setDeadlineDate(dto.getDeadlineDate());
        task.setGroup(group);
        task.setAssignedUser(assignedUser);

        return mapper.toDto(groupTaskRepository.save(task));
    }

    public GroupTaskDtoResponse update(Long id, GroupTaskDtoRequest dto) {
        GroupTask task = groupTaskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Задача не найдена"));

        User currentUser = getCurrentUser();
        if (!task.getGroup().getUsers().contains(currentUser) && !isAdmin(currentUser)) {
            throw new NotFoundException("Вы не можете редактировать эту задачу");
        }

        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getContent() != null) task.setContent(dto.getContent());
        if (dto.getIsCompleted() != null) task.setIsCompleted(dto.getIsCompleted());
        if (dto.getIsImportant() != null) task.setIsImportant(dto.getIsImportant());
        if (dto.getDeadlineDate() != null) task.setDeadlineDate(dto.getDeadlineDate());
        if (dto.getAssignedUserId() != null) {

            if (dto.getAssignedUserId() == -1)
                task.setAssignedUser(null);
            else {
                User temp = userRepository.findById(dto.getAssignedUserId())
                        .orElseThrow(() -> new NotFoundException(
                                String.format("Пользователь с таким ID не найден", id)));
                task.setAssignedUser(temp);
            }

        }

        return mapper.toDto(groupTaskRepository.save(task));
    }

    public GroupTaskDtoResponse findById(Long id) {
        GroupTask task = groupTaskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Задача не найдена"));

        User currentUser = getCurrentUser();
        if (!task.getGroup().getUsers().contains(currentUser) && !isAdmin(currentUser)) {
            throw new NotFoundException("Вы не можете просматривать эту задачу");
        }

        return mapper.toDto(task);
    }

    public List<GroupTaskDtoResponse> findByGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Группа не найдена"));

        User currentUser = getCurrentUser();
        if (!group.getUsers().contains(currentUser) && !isAdmin(currentUser)) {
            throw new NotFoundException("Вы не можете просматривать задачи этой группы");
        }

        return groupTaskRepository.findByGroupId(groupId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<GroupTaskDtoResponse> findByAssignedUser(Long userId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId) && !isAdmin(currentUser)) {
            throw new NotFoundException("Вы можете просматривать только свои задачи");
        }
        return groupTaskRepository.findByAssignedUserId(userId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public void delete(Long id) {
        GroupTask task = groupTaskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Задача не найдена"));

        User currentUser = getCurrentUser();
        if (!task.getGroup().getUsers().contains(currentUser) && !isAdmin(currentUser)) {
            throw new NotFoundException("Вы не можете удалять эту задачу");
        }

        groupTaskRepository.delete(task);
    }

    public void assignUser(Long taskId, Long userId) {
        GroupTask task = groupTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Задача не найдена"));

        Group group = task.getGroup();
        User currentUser = getCurrentUser();

        if (!group.getUsers().contains(currentUser) && !isAdmin(currentUser)) {
            throw new NotFoundException("Вы не состоите в этой группе");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!group.getUsers().contains(user)) {
            throw new NotFoundException("Пользователь не состоит в этой группе");
        }

        task.setAssignedUser(user);
        groupTaskRepository.save(task);
    }

    public void unassignUser(Long taskId) {
        GroupTask task = groupTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Задача не найдена"));

        User currentUser = getCurrentUser();
        if (!task.getGroup().getUsers().contains(currentUser) && !isAdmin(currentUser)) {
            throw new NotFoundException("Вы не можете управлять этой задачей");
        }

        task.setAssignedUser(null);
        groupTaskRepository.save(task);
    }
}
