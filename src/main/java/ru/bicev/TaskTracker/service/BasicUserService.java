package ru.bicev.TaskTracker.service;

import java.nio.file.AccessDeniedException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ru.bicev.TaskTracker.dto.UserDto;
import ru.bicev.TaskTracker.entity.User;
import ru.bicev.TaskTracker.repo.UserRepository;
import ru.bicev.TaskTracker.util.Role;
import ru.bicev.TaskTracker.util.UserMapper;

@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BasicUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.fromDto(userDto);
        if (userDto.getRole() == null) {
            user.setRole(Role.USER);
        }
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User createdUser = userRepository.save(user);
        return UserMapper.fromEntity(createdUser);

    }

    @Transactional
    @Override
    public void deleteUser(Long userId, Principal principal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " is not found"));

        User currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));

        if (!currentUser.getId().equals(user.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You are not allowed to delete this user");
        }

        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " is not found"));
        return UserMapper.fromEntity(user);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " is not found"));
        return UserMapper.fromEntity(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(Long userId, UserDto userDto, Principal principal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " is not found"));
        User currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));

        if (!currentUser.getId().equals(user.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You are not allowed to edit this user");
        }
        if (userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        user.setRole(Role.fromString(userDto.getRole()));
        User updatedUser = userRepository.save(user);
        return UserMapper.fromEntity(updatedUser);
    }

}
