package com.vlad.todo.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TaskDtoRequest> tasksDtoRequest;
}
