package com.example.tattooplatform.configuration.security.repos;

import com.example.tattooplatform.configuration.security.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends CrudRepository<Role, UUID> {
    Role findByName(String name);
}
