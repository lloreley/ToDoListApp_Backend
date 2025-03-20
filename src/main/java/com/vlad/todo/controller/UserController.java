package com.vlad.todo.controller;


import com.vlad.todo.dto.UserDtoRequest;
import com.vlad.todo.dto.UserDtoResponse;
import com.vlad.todo.exception.CreationException;
import com.vlad.todo.exception.NotFoundException;
import com.vlad.todo.exception.UpdateException;
import com.vlad.todo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDtoResponse> allUsers() {
        return userService.findAll();
    }

    @PostMapping("/saveUser")
    public UserDtoResponse saveUser(@RequestBody UserDtoRequest userDtoRequest) {
        return userService.save(userDtoRequest);
    }

    @GetMapping("/{id}")
    public UserDtoResponse findUserById(@PathVariable long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    public UserDtoResponse updateUser(@PathVariable long id, @RequestBody UserDtoRequest userDtoRequest) {
        return userService.updateUser(id, userDtoRequest);
    }

    @DeleteMapping("/deleteUser/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUserById(id);
    }

    @ExceptionHandler(CreationException.class)
    public ResponseEntity<String> handleCreateException(CreationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(UpdateException.class)
    public ResponseEntity<String> handleUpdateException(UpdateException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}
