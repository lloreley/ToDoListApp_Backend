package com.vlad.todo.service;

import com.vlad.todo.dto.UserDtoRequest;
import com.vlad.todo.dto.UserDtoResponse;
import com.vlad.todo.exception.CreationException;
import com.vlad.todo.exception.NotFoundException;
import com.vlad.todo.exception.UpdateException;
import com.vlad.todo.mapper.UserMapper;
import com.vlad.todo.model.UserEntity;
import com.vlad.todo.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public List<UserDtoResponse> findAll() {
        List<UserDtoResponse> usersDtoResponse = new ArrayList<>();
        userRepository.findAll().forEach(
                userEntity -> usersDtoResponse.add(userMapper.toDto(userEntity)));
        return usersDtoResponse;
    }

    public UserDtoResponse findById(long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id %d not found", id)));
        return userMapper.toDto(userEntity);
    }

    public UserDtoResponse save(UserDtoRequest userDtoRequest) {
        if (userRepository.existsByEmail(userDtoRequest.getEmail())
                || userRepository.existsByPhone(userDtoRequest.getPhone())) {
            throw new CreationException("User with the same email/phone already exists");
        }
        UserEntity userEntity = userMapper.toEntity(userDtoRequest);
        userRepository.save(userEntity);
        return userMapper.toDto(userEntity);
    }

    public UserDtoResponse updateUser(long id, UserDtoRequest userDtoRequest) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id %d not found", id)));

        if (userDtoRequest.getEmail() != null) {
            userEntity.setEmail(userDtoRequest.getEmail());
        }
        if (userDtoRequest.getPhone() != null) {
            userEntity.setPhone(userDtoRequest.getPhone());
        }
        if (userDtoRequest.getLastName() != null) {
            userEntity.setLastName(userDtoRequest.getLastName());
        }
        if (userDtoRequest.getFirstName() != null) {
            userEntity.setFirstName(userDtoRequest.getFirstName());
        }
        try {
            userRepository.save(userEntity);
            return userMapper.toDto(userEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new UpdateException("Error updating user with id " + id);
        }
    }

    public void deleteUserById(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(
                    String.format("User with id %d not found.", id));
        }
        userRepository.deleteById(id);
    }

}
