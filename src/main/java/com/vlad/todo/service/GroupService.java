package com.vlad.todo.service;

import com.vlad.todo.dto.GroupDtoRequest;
import com.vlad.todo.dto.GroupDtoResponse;
import com.vlad.todo.dto.UserDtoResponse;
import com.vlad.todo.exception.InvalidInputException;
import com.vlad.todo.exception.NotFoundException;
import com.vlad.todo.mapper.GroupMapper;
import com.vlad.todo.mapper.UserMapper;
import com.vlad.todo.model.Group;
import com.vlad.todo.model.User;
import com.vlad.todo.repository.GroupRepository;
import com.vlad.todo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class GroupService {

    public static final String GROUP_WITH_ID_NOT_FOUND = "Группа с id %d не найдена";

    private final GroupMapper groupMapper;
    private final UserMapper userMapper;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Текущий пользователь не найден"));
    }

    private boolean isAdmin(User user) {
        return user.getRole().name().equals("ADMIN");
    }

    private void requireGroupOwnerOrAdmin(User user, Group group) {
        if (isAdmin(user)) return;

        boolean isMember = group.getUsers().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));

        if (!isMember) {
            throw new InvalidInputException("Вы не состоите в этой группе");
        }
    }

    public List<GroupDtoResponse> findAll() {
        User user = getCurrentUser();

        if (isAdmin(user)) {
            return groupRepository.findAll().stream()
                    .map(groupMapper::toDto)
                    .toList();
        }

        return user.getGroups().stream()
                .map(groupMapper::toDto)
                .toList();
    }

    public GroupDtoResponse findById(long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(GROUP_WITH_ID_NOT_FOUND, id)));

        requireGroupOwnerOrAdmin(getCurrentUser(), group);

        return groupMapper.toDto(group);
    }

    public GroupDtoResponse save(GroupDtoRequest request) {
        User current = getCurrentUser();

        Group group = groupMapper.toEntity(request);

        if (!isAdmin(current)) {
            group.addUser(current);
        }

        groupRepository.save(group);
        return groupMapper.toDto(group);
    }

    public GroupDtoResponse update(long id, GroupDtoRequest request) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(GROUP_WITH_ID_NOT_FOUND, id)));

        requireGroupOwnerOrAdmin(getCurrentUser(), group);

        if (request.getName() != null) {
            group.setName(request.getName());
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }

        groupRepository.save(group);
        return groupMapper.toDto(group);
    }

    public void deleteById(long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(GROUP_WITH_ID_NOT_FOUND, id)));

        requireGroupOwnerOrAdmin(getCurrentUser(), group);

        group.getUsers().forEach(u -> u.getGroups().remove(group));
        groupRepository.delete(group);
    }

    public GroupDtoResponse addUserToGroup(long groupId, long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(GROUP_WITH_ID_NOT_FOUND, groupId)));

        requireGroupOwnerOrAdmin(getCurrentUser(), group);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        group.addUser(user);
        groupRepository.save(group);

        return groupMapper.toDto(group);
    }

    public GroupDtoResponse removeUserFromGroup(long groupId, long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(GROUP_WITH_ID_NOT_FOUND, groupId)));

        User current = getCurrentUser();

        requireGroupOwnerOrAdmin(current, group);

        User target = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!isAdmin(current) && !current.getId().equals(userId)) {
            throw new InvalidInputException("Вы можете удалять только себя из группы");
        }

        group.removeUser(target);
        groupRepository.save(group);

        return groupMapper.toDto(group);
    }
}
