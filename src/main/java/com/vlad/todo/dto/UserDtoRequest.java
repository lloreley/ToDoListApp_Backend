package com.vlad.todo.dto;


import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDtoRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String role; // USER / ADMIN
}