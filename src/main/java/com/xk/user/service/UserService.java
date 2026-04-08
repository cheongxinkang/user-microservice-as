package com.xk.user.service;

import com.xk.user.dto.UserDTO;

import java.util.Optional;

public interface UserService {

    Optional<UserDTO> createUser(String username, String password, String userType);

    Optional<UserDTO> findByUsername(String username);

    boolean validateUser(String username, String password);

}
