package com.bsingh.doto.Task.Manager.Repository;

import com.bsingh.doto.Task.Manager.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepo extends MongoRepository<User, String> {
   Optional<User>findByEmail(String email);
   boolean  existsByEmail(String email);
}
