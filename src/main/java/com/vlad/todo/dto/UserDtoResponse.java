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
public class UserDtoResponse extends UserDtoRequest {
    private long id;
    private List<TaskDtoResponse> tasksDtoResponse;
}
