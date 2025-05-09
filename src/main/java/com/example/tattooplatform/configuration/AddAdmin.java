package com.example.tattooplatform.configuration;

import com.example.tattooplatform.configuration.security.services.RoleService;
import com.example.tattooplatform.configuration.security.model.User;
import com.example.tattooplatform.configuration.security.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("!test")
@Component
public class AddAdmin implements CommandLineRunner {
    private final RoleService roleService;
    private final UserService userService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    public AddAdmin(RoleService roleService, UserService userService){
        this.roleService = roleService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        roleService.saveRoleByGivenName("Admin");
        addAdminUser();
    }

    private void addAdminUser() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userService.saveUserIfItDoesNotAlreadyExist(User.builder()
                .enabled(true)
                .password(encoder.encode(adminPassword))
                .username(adminUsername)
                .roles(List.of(roleService.findRoleByName("Admin")))
                .build());
    }

}
