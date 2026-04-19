package com.vaibhav.ecom.UserMicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vaibhav.ecom.UserMicroservice.entity.User;
import com.vaibhav.ecom.UserMicroservice.repo.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        // Perform validation, hashing password, etc.
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User updateUser(User user) {
        // Perform validation, etc.
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public User syncFromOidc(String externalSub, String email, String username) {
        return userRepository.findByExternalSub(externalSub).map(u -> {
            u.setEmail(email);
            if (username != null && !username.isBlank()) {
                u.setUsername(username);
            }
            return userRepository.save(u);
        }).orElseGet(() -> {
            User u = new User();
            u.setExternalSub(externalSub);
            u.setEmail(email);
            u.setUsername(username != null && !username.isBlank() ? username : email);
            u.setPassword("{noop}oauth-user");
            return userRepository.save(u);
        });
    }
}
