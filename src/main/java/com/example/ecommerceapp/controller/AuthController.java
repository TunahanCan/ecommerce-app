package com.example.ecommerceapp.controller;

import com.example.ecommerceapp.config.security.JwtUtil;
import com.example.ecommerceapp.model.dto.LoginRequestDTO;
import com.example.ecommerceapp.model.dto.RegistrationRequestDTO;
import com.example.ecommerceapp.model.entities.User;
import com.example.ecommerceapp.model.enums.Role;
import com.example.ecommerceapp.repositories.UserRepository;
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
     * Kullanıcı kayıt olma Endpointi
     *
     * @param registrationRequest Kullanıcı kayıt bilgilerini içeren DTO
     * @return Başarılı veya başarısız mesajı ile ApiResponse
     */
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
     * Kullanıcı giriş yapma Endpointi
     *
     * @param loginRequest Kullanıcı giriş bilgilerini içeren DTO
     * @return JWT token'ı içeren ApiResponse
     */
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
