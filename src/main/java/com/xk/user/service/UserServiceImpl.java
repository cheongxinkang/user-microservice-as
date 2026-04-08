package com.xk.user.service;

import com.xk.user.dto.UserConverter;
import com.xk.user.dto.UserDTO;
import com.xk.user.entity.UserEntity;
import com.xk.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserDTO> createUser(String username, String password, String userType) {

        if (userRepository.findByUsername(username).isPresent()) {
            return Optional.empty();
        }

        return Optional.of(userRepository.save(
            new UserEntity(UUID.randomUUID(), username, passwordEncoder.encode(password), userType)
        )).map(UserConverter::fromUserEntity);
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(UserConverter::fromUserEntity);
    }

    @Override
    public boolean validateUser(String username, String password) {
        return userRepository.findByUsername(username)
            .map(user ->
                passwordEncoder.matches(password, user.getPassword()))
            .orElse(false);
    }

}
