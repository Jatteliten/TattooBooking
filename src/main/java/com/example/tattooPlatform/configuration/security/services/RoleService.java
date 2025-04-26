package com.example.tattooPlatform.configuration.security.services;

import com.example.tattooPlatform.configuration.security.model.Role;
import com.example.tattooPlatform.configuration.security.repos.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    public Role findRoleByName(String roleName){
        return roleRepository.findByName(roleName);
    }

    public void saveRoleByGivenName(String roleName){
        if(findRoleByName(roleName) == null){
            roleRepository.save(Role.builder().name(roleName).build());
        }
    }
}
