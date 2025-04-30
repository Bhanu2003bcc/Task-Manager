package com.bsingh.doto.Task.Manager.Controller;

import com.bsingh.doto.Task.Manager.DTO.TodoDto;
import com.bsingh.doto.Task.Manager.Service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    @GetMapping
    public ResponseEntity<List<TodoDto>> getAllTodos() {
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDto> getTodoById(@PathVariable String id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    @PostMapping
    public ResponseEntity<TodoDto> createTodo(
            @Valid @RequestBody TodoDto todoDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(todoService.createTodo(todoDto, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodo(
            @PathVariable String id,
            @Valid @RequestBody TodoDto todoDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(todoService.updateTodo(id, todoDto, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        todoService.deleteTodo(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-todos")
    public ResponseEntity<List<TodoDto>> getMyTodos(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(todoService.getTodosByUser(userDetails.getUsername()));
    }

    @GetMapping("/assigned-todos")
    public ResponseEntity<List<TodoDto>> getAssignedTodos(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(todoService.getAssignedTodos(userDetails.getUsername()));
    }
}
