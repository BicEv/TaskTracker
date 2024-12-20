package ru.bicev.TaskTracker.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.bicev.TaskTracker.entity.UserEntity;
import ru.bicev.TaskTracker.repo.UserEntityRepository;

@Service
public class UserService {

    private final UserEntityRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserEntityRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity registerUser(UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

}
