package com.example.demo.security;

import org.springframework.stereotype.Service;

@Service
public class RoleService {
    RoleRepository roleRepository;

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
