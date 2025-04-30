package com.bsingh.doto.Task.Manager.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "Todos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Todo {
    @Id
    private String id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @DBRef
    private User createdBy;

    @DBRef
    private Set<User> assignedTo = new HashSet<>();

    @DBRef
    private Set<User> tags = new HashSet<>();

    private Priority priority;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}