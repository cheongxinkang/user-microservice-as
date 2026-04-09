package com.xk.user.controller;

import com.xk.user.dto.TokenRequest;
import com.xk.user.dto.TokenResponse;
import com.xk.user.dto.UserDTO;
import com.xk.user.service.ClientService;
import com.xk.user.service.JWTService;
import com.xk.user.service.ScopeService;
import com.xk.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ClientService clientService;
    private final JWTService jwtService;
    private final ScopeService scopeService;

    public UserController(UserService userService, ClientService clientService, JWTService jwtService, ScopeService scopeService) {
        this.userService = userService;
        this.clientService = clientService;
        this.jwtService = jwtService;
        this.scopeService = scopeService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO.username(), userDTO.password(), userDTO.userType())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(
            value = "/token",
            produces = "application/json",
            consumes = "application/json")
    public ResponseEntity<TokenResponse> createToken(@RequestBody TokenRequest tokenRequest) {

        if (!"password".equals(tokenRequest.grant_type())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!clientService.validateClient(tokenRequest.client_id(), tokenRequest.client_secret())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!userService.validateUser(tokenRequest.username(), tokenRequest.password())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDTO foundUser = userService.findByUsername(tokenRequest.username()).get();

        return ResponseEntity.ok(
                jwtService.getJWTToken(
                        tokenRequest,
                        scopeService.findScope(foundUser.userType()),
                        foundUser.id())
                );
    }

}
