package com.vlad.todo.controller;

import com.vlad.todo.dto.GroupTaskDtoRequest;
import com.vlad.todo.dto.GroupTaskDtoResponse;
import com.vlad.todo.service.GroupTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/group-tasks")
@CrossOrigin(origins = "http://localhost:8081")
public class GroupTaskController {

    private final GroupTaskService groupTaskService;


    @PostMapping
    public ResponseEntity<GroupTaskDtoResponse> create(@RequestBody GroupTaskDtoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupTaskService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupTaskDtoResponse> update(
            @PathVariable Long id,
            @RequestBody GroupTaskDtoRequest dto
    ) {
        return ResponseEntity.ok(groupTaskService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupTaskDtoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(groupTaskService.findById(id));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<GroupTaskDtoResponse>> findByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupTaskService.findByGroup(groupId));
    }

    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<GroupTaskDtoResponse>> findByAssignedUser(@PathVariable Long userId) {
        return ResponseEntity.ok(groupTaskService.findByAssignedUser(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        groupTaskService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<Void> assignUser(@PathVariable Long taskId, @PathVariable Long userId) {
        groupTaskService.assignUser(taskId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{taskId}/unassign")
    public ResponseEntity<Void> unassignUser(@PathVariable Long taskId) {
        groupTaskService.unassignUser(taskId);
        return ResponseEntity.ok().build();
    }
}
