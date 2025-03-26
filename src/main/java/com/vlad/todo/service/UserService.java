package com.vlad.todo.service;

import com.vlad.todo.cache.UserCache;
import com.vlad.todo.dto.UserDtoRequest;
import com.vlad.todo.dto.UserDtoResponse;
import com.vlad.todo.exception.CreationException;
import com.vlad.todo.exception.NotFoundException;
import com.vlad.todo.exception.UpdateException;
import com.vlad.todo.mapper.UserMapper;
import com.vlad.todo.model.Group;
import com.vlad.todo.model.User;
import com.vlad.todo.repository.GroupRepository;
import com.vlad.todo.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserCache userCache;

    public List<UserDtoResponse> findAll() {
        List<UserDtoResponse> usersDtoResponse = new ArrayList<>();
        userRepository.findAll().forEach(
                user -> usersDtoResponse.add(userMapper.toDto(user)));
        return usersDtoResponse;
    }

    public void addUserToGroup(long userId, long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Group with id %d does not exist", groupId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id %d does not exist", userId)));
        group.addUser(user);
        groupRepository.save(group);
    }

    public void removeUserFromGroup(long userId, long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Group with id %d does not exist", groupId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id %d does not exist", userId)));
        group.removeUser(user);
        groupRepository.save(group);
    }

    public UserDtoResponse findById(long id) {
        UserDtoResponse cachedUser = userCache.get(id);
        if (cachedUser != null) {
            return cachedUser;
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id %d not found", id)));
        userCache.put(id, userMapper.toDto(user));
        return userMapper.toDto(user);
    }

    public UserDtoResponse save(UserDtoRequest userDtoRequest) {
        if (userRepository.existsByEmail(userDtoRequest.getEmail())
                || userRepository.existsByPhone(userDtoRequest.getPhone())) {
            throw new CreationException("User with the same email/phone already exists");
        }
        User user = userMapper.toEntity(userDtoRequest);
        userRepository.save(user);
        userCache.put(user.getId(), userMapper.toDto(user));
        return userMapper.toDto(user);
    }

    public UserDtoResponse updateUser(long id, UserDtoRequest userDtoRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id %d not found", id)));

        if (userDtoRequest.getEmail() != null) {
            user.setEmail(userDtoRequest.getEmail());
        }
        if (userDtoRequest.getPhone() != null) {
            user.setPhone(userDtoRequest.getPhone());
        }
        if (userDtoRequest.getLastName() != null) {
            user.setLastName(userDtoRequest.getLastName());
        }
        if (userDtoRequest.getFirstName() != null) {
            user.setFirstName(userDtoRequest.getFirstName());
        }
        try {
            userRepository.save(user);
            userCache.put(user.getId(), userMapper.toDto(user));
            return userMapper.toDto(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UpdateException("Error updating user with id " + id);
        }
    }

    public void deleteUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id %d not found.", id)));
        user.getGroups().forEach(group -> group.getUsers().remove(user));
        userCache.remove(id);
        userRepository.deleteById(id);
    }

    public List<UserDtoResponse> findUsersByGroup(long groupId) {
        List<User> users = userRepository.findUsersByGroup(groupId);
        List<UserDtoResponse> usersDtoResponse = new ArrayList<>();
        users.forEach(user -> usersDtoResponse.add(userMapper.toDto(user)));
        return usersDtoResponse;
    }
}
