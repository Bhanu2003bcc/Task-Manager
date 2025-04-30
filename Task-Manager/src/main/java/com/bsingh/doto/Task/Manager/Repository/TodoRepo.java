package com.bsingh.doto.Task.Manager.Repository;

import com.bsingh.doto.Task.Manager.Entity.Todo;
import com.bsingh.doto.Task.Manager.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TodoRepo extends MongoRepository<Todo, String> {
    List<Todo> findByCreatedBy(User user);
    List<Todo> findByAssignedToContaining(User user);
    List<Todo> findByCompleted(boolean completed);
}
