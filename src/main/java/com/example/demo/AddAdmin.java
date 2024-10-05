package com.example.demo;

import com.example.demo.security.RoleService;
import com.example.demo.security.User;
import com.example.demo.security.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("!test")
@Component
public class AddAdmin implements CommandLineRunner {

    RoleService roleService;

    UserService userService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    private final String adminString = "admin";

    public AddAdmin(RoleService roleService, UserService userService){
        this.roleService = roleService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        roleService.saveRoleByGivenName(adminString);
        addAdminUser();
    }

    private void addAdminUser() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println(userService.saveUserIfItDoesNotAlreadyExist(User.builder()
                .enabled(true)
                .password(encoder.encode(adminPassword))
                .username(adminUsername)
                .roles(List.of(roleService.findRoleByName(adminString)))
                .build()));
    }

}