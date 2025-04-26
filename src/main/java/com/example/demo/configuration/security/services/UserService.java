package com.example.demo.configuration.security.services;

import com.example.demo.configuration.security.model.User;
import com.example.demo.configuration.security.repos.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public User findUserByUserName(String name){
        return userRepository.findByUsername(name);
    }

    public void saveUserIfItDoesNotAlreadyExist(User user){
        if(findUserByUserName(user.getUsername()) == null){
            saveUser(user);
        }
    }
}
