package com.bsingh.doto.Task.Manager.Repository;

import com.bsingh.doto.Task.Manager.Entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepo extends MongoRepository<Role, String> {
    Optional<Role> findByName(Role.RoleName name);
}
