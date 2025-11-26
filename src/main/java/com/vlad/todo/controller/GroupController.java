package com.vlad.todo.controller;

import com.vlad.todo.dto.GroupDtoRequest;
import com.vlad.todo.dto.GroupDtoResponse;
import com.vlad.todo.dto.UserDtoResponse;
import com.vlad.todo.exception.InvalidInputException;
import com.vlad.todo.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Группы", description = "API для управления группами пользователей")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "Получить все группы",
            description = "ADMIN — все группы, USER — только группы, в которых он состоит")
    @GetMapping
    public ResponseEntity<List<GroupDtoResponse>> getAllGroups() {
        return ResponseEntity.ok(groupService.findAll());
    }

    @Operation(summary = "Получить группу по ID",
            description = "Возвращает группу. USER — только если состоит в группе.")
    @GetMapping("/{id}")
    public ResponseEntity<GroupDtoResponse> findGroupById(@PathVariable long id) {
        if (id < 1) throw new InvalidInputException("ID должен быть больше 0");
        return ResponseEntity.ok(groupService.findById(id));
    }

    @Operation(summary = "Создать группу",
            description = "ADMIN — создаёт любую группу, USER — создаёт группу и становится её участником")
    @PostMapping
    public ResponseEntity<GroupDtoResponse> saveGroup(@Valid @RequestBody GroupDtoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.save(request));
    }

    @Operation(summary = "Обновить группу",
            description = "ADMIN — всегда, USER — только если состоит в группе")
    @PutMapping("/{id}")
    public ResponseEntity<GroupDtoResponse> updateGroup(
            @PathVariable long id,
            @RequestBody GroupDtoRequest request) {

        if (id < 1) throw new InvalidInputException("ID должен быть больше 0");

        return ResponseEntity.ok(groupService.update(id, request));
    }

    @Operation(summary = "Удалить группу",
            description = "ADMIN — всегда, USER — только если состоит в группе")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable long id) {
        if (id < 1) throw new InvalidInputException("ID должен быть больше 0");
        groupService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Добавить пользователя в группу",
            description = "ADMIN — любого, USER — только в свою группу")
    @PostMapping("/{groupId}/addUser/{userId}")
    public ResponseEntity<GroupDtoResponse> addUserToGroup(
            @PathVariable long groupId,
            @PathVariable long userId) {

        return ResponseEntity.ok(groupService.addUserToGroup(groupId, userId));
    }

    @Operation(summary = "Удалить пользователя из группы",
            description = "ADMIN — любого, USER — только себя")
    @DeleteMapping("/{groupId}/removeUser/{userId}")
    public ResponseEntity<GroupDtoResponse> removeUserFromGroup(
            @PathVariable long groupId,
            @PathVariable long userId) {

        return ResponseEntity.ok(groupService.removeUserFromGroup(groupId, userId));
    }
}
