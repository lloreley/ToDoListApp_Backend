package com.vlad.todo.mapper;


import com.vlad.todo.dto.*;
import com.vlad.todo.model.*;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {


    public UserDtoResponse toDto(User user) {
        return UserDtoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .build();
    }


    public User toEntity(UserDtoRequest dto) {
        return User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword()) // hashed later
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .role(Role.valueOf(dto.getRole()))
                .build();
    }
}