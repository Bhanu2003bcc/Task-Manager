package com.bsingh.doto.Task.Manager.DTO;

import com.bsingh.doto.Task.Manager.Entity.Todo;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {
    private String id;

    @NotBlank
    private String title;

    private String description;
    private boolean completed;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdById;
    private Set<String> assignedToIds;
    private Set<String> tagUserIds;  // Changed from 'tags' to be explicit
    private Todo.Priority priority;
}