package com.example.ecommerceapp;

import com.example.ecommerceapp.model.dto.LoginRequestDTO;
import com.example.ecommerceapp.model.dto.RegistrationRequestDTO;
import com.example.ecommerceapp.model.entities.User;
import com.example.ecommerceapp.model.enums.Role;
import com.example.ecommerceapp.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Nested
    class RegisterUserTests {
        @Test
        void register_user_success() throws Exception {
            //given
            RegistrationRequestDTO registrationRequest = new RegistrationRequestDTO(
                    "John", "Doe", "john.doe@example.com", "password123"
            );
            //when
            MvcResult result = mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registrationRequest)))
                    .andExpect(status().isCreated())
                    .andReturn();

            //then
            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).contains("User registered successfully");
        }

        @Test
        void register_user_user_already_exists() throws Exception {
            //given
            User existingUser = new User();
            existingUser.setFirstName("Jane");
            existingUser.setLastName("Doe");
            existingUser.setEmail("jane.doe@example.com");
            existingUser.setPassword(passwordEncoder.encode("password123"));
            userRepository.save(existingUser);

            //when
            RegistrationRequestDTO registrationRequest = new RegistrationRequestDTO(
                    "Jane", "Doe", "jane.doe@example.com", "password123"
            );

            MvcResult result = mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registrationRequest)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //then
            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).contains("User with this email already exists");
        }
    }

    @Nested
    class AuthenticateUserTests {
        @Test
        void authenticate_user_success() throws Exception {
            //given
            User user = new User();
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setEmail("john.doe@example.com");
            user.setPassword(passwordEncoder.encode("password123"));
            user.setRole(Role.EXPERT);
            userRepository.save(user);

            //when
            LoginRequestDTO loginRequest = new LoginRequestDTO("john.doe@example.com", "password123");

            MvcResult result = mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            //then
            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).contains("token");
        }

        @Test
        void authenticate_user_invalid_credentials() throws Exception {
            //given
            User user = new User();
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setEmail("john.doe@example.com");
            user.setPassword(passwordEncoder.encode("password123"));
            user.setRole(Role.EXPERT);
            userRepository.save(user);

            //when
            LoginRequestDTO loginRequest = new LoginRequestDTO("john.doe@example.com", "wrongpassword");

            MvcResult result = mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andReturn();

            //then
            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).contains("Invalid credentials");
        }
    }
}