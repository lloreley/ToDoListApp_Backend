package com.vlad.todo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoRequest {
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phone;
    protected List<TaskDtoResponse> tasks;
}
