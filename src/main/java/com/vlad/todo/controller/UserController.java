package com.vlad.todo.controller;

import com.vlad.todo.dto.UserDtoRequest;
import com.vlad.todo.dto.UserDtoResponse;
import com.vlad.todo.model.Role;
import com.vlad.todo.service.GroupService;
import com.vlad.todo.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:8081")
@Tag(name = "Пользователи", description = "API для управления пользователями")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final GroupService groupService;

    @Autowired
    public UserController(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<UserDtoResponse>> allUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDtoResponse> findUserById(@PathVariable long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping("/{userId}/add/{groupId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> addUserToGroup(@PathVariable long userId, @PathVariable long groupId) {
        userService.addUserToGroup(userId, groupId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDtoResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = userDetails.getUsername();
        UserDtoResponse user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/saveUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDtoResponse> saveUser(@Valid @RequestBody UserDtoRequest userDtoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userDtoRequest));
    }

    @GetMapping("/by-group/{groupName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDtoResponse>> findUsersByGroup(@PathVariable String groupName) {
        return ResponseEntity.ok(userService.findUsersByGroup(groupName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDtoResponse> updateUser(@PathVariable long id,
                                                      @RequestBody UserDtoRequest userDtoRequest) {

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDtoResponse currentUser = userService.findByEmail(currentEmail);

        if (!currentUser.getRole().equals(Role.ADMIN) && currentUser.getId() != id) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(userService.updateUser(id, userDtoRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/remove/{groupId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeUserFromGroup(@PathVariable long userId,
                                                    @PathVariable long groupId) {
        userService.removeUserFromGroup(userId, groupId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/saveAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDtoResponse>> saveAllUsers(
            @Valid @RequestBody List<UserDtoRequest> userDtoRequests) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveAll(userDtoRequests));
    }
}
