package com.bsingh.doto.Task.Manager.Service;

import com.bsingh.doto.Task.Manager.DTO.TodoDto;
import com.bsingh.doto.Task.Manager.Entity.Role;
import com.bsingh.doto.Task.Manager.Entity.Todo;
import com.bsingh.doto.Task.Manager.Entity.User;
import com.bsingh.doto.Task.Manager.Exception.ResourceNotFoundException;
import com.bsingh.doto.Task.Manager.Repository.TodoRepo;
import com.bsingh.doto.Task.Manager.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepo todoRepository;
    private final UserRepo userRepository;

    // Get all Todos
    public List<TodoDto> getAllTodos() {
        return todoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get Todo by ID
    public TodoDto getTodoById(String id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));
        return convertToDto(todo);
    }

    // Create new Todo
    public TodoDto createTodo(TodoDto todoDto, String userEmail) {
        User createdBy = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Set<User> assignedTo = convertIdsToUsers(todoDto.getAssignedToIds());
        Set<User> tagUsers = convertIdsToUsers(todoDto.getTagUserIds());

        Todo todo = Todo.builder()
                .title(todoDto.getTitle())
                .description(todoDto.getDescription())
                .completed(false)
                .dueDate(todoDto.getDueDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .assignedTo(assignedTo)
                .tags(tagUsers)
                .priority(todoDto.getPriority())
                .build();

        Todo savedTodo = todoRepository.save(todo);
        return convertToDto(savedTodo);
    }

    // Update existing Todo
    public TodoDto updateTodo(String id, TodoDto todoDto, String userEmail) {
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        if (!existingTodo.getCreatedBy().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName() == Role.RoleName.ROLE_ADMIN)) {
            throw new SecurityException("Not authorized to update this todo");
        }

        Set<User> assignedTo = convertIdsToUsers(todoDto.getAssignedToIds());
        Set<User> tagUsers = convertIdsToUsers(todoDto.getTagUserIds());

        existingTodo.setTitle(todoDto.getTitle());
        existingTodo.setDescription(todoDto.getDescription());
        existingTodo.setCompleted(todoDto.isCompleted());
        existingTodo.setDueDate(todoDto.getDueDate());
        existingTodo.setUpdatedAt(LocalDateTime.now());
        existingTodo.setAssignedTo(assignedTo);
        existingTodo.setTags(tagUsers);
        existingTodo.setPriority(todoDto.getPriority());

        Todo updatedTodo = todoRepository.save(existingTodo);
        return convertToDto(updatedTodo);
    }

    // Delete Todo
    public void deleteTodo(String id, String userEmail) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        if (!todo.getCreatedBy().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName() == Role.RoleName.ROLE_ADMIN)) {
            throw new SecurityException("Not authorized to delete this todo");
        }

        todoRepository.delete(todo);
    }

    // Get Todos created by user
    public List<TodoDto> getTodosByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        return todoRepository.findByCreatedBy(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get Todos assigned to user
    public List<TodoDto> getAssignedTodos(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        return todoRepository.findByAssignedToContaining(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Convert Todo entity to DTO
    private TodoDto convertToDto(Todo todo) {
        return TodoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .completed(todo.isCompleted())
                .dueDate(todo.getDueDate())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .createdById(todo.getCreatedBy().getId())
                .assignedToIds(convertUsersToIds(todo.getAssignedTo()))
                .tagUserIds(convertUsersToIds(todo.getTags()))
                .priority(todo.getPriority())
                .build();
    }

    // Helper: Convert user IDs to User objects
    private Set<User> convertIdsToUsers(Set<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashSet<>();
        }

        List<User> users = userRepository.findAllById(userIds);

        // Verify all users were found
        if (users.size() != userIds.size()) {
            Set<String> foundIds = users.stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());

            Set<String> missingIds = userIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());

            throw new ResourceNotFoundException("Users not found with IDs: " + missingIds);
        }

        return new HashSet<>(users);
    }

    // Helper: Convert User objects to IDs
    private Set<String> convertUsersToIds(Set<User> users) {
        if (users == null || users.isEmpty()) {
            return new HashSet<>();
        }
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    }
}