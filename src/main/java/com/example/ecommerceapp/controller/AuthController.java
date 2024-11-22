package com.example.ecommerceapp.controller;

import com.example.ecommerceapp.config.security.JwtUtil;
import com.example.ecommerceapp.model.dto.LoginRequestDTO;
import com.example.ecommerceapp.model.dto.RegistrationRequestDTO;
import com.example.ecommerceapp.model.entities.User;
import com.example.ecommerceapp.model.enums.Role;
import com.example.ecommerceapp.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Endpoint for user registration.
     *
     * @param registrationRequest DTO containing user registration details.
     * @return Response containing success or failure message.
     */
    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "User with this email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody RegistrationRequestDTO registrationRequest) {

        if (userRepository.findByEmail(registrationRequest.email()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "User with this email already exists"));
        }
        User user = new User();
        user.setFirstName(registrationRequest.firstName());
        user.setLastName(registrationRequest.lastName());
        user.setEmail(registrationRequest.email());
        user.setRole(Role.EXPERT);
        user.setPassword(passwordEncoder.encode(registrationRequest.password()));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully"));
    }


    /**
     * Endpoint for user authentication.
     *
     * @param loginRequest DTO containing user login credentials.
     * @return Response containing JWT token or error message.
     */
    @Operation(summary = "Authenticate a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );
            User user = (User) authentication.getPrincipal();
            String token = jwtUtil.generateJwtToken(user);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }
    }
}
