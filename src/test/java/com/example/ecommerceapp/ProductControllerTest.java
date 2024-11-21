package com.example.ecommerceapp;

import com.example.ecommerceapp.model.dto.LoginRequestDTO;
import com.example.ecommerceapp.model.dto.ProductRequestDTO;
import com.example.ecommerceapp.model.dto.RegistrationRequestDTO;
import com.example.ecommerceapp.model.entities.Product;
import com.example.ecommerceapp.repositories.UserRepository;
import com.example.ecommerceapp.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductService productService;

    private String jwtToken;

    private void loginForTest() throws Exception {
        // User Registration
        RegistrationRequestDTO registrationRequest = new RegistrationRequestDTO(
                "John", "Doe", "john.doe@example.com", "password123"
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated());

        // User Login and Get Token
        LoginRequestDTO loginRequest = new LoginRequestDTO("john.doe@example.com", "password123");
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract Token
        String responseContent = result.getResponse().getContentAsString();
        this.jwtToken = objectMapper.readTree(responseContent).get("token").asText();
    }

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        loginForTest();
    }

    @AfterEach
    public void tearDown() throws Exception {
        Objects.requireNonNull(cacheManager.getCache("productList")).clear();
        userRepository.deleteAll();
    }

    @Nested
    class GetAllProductsTests {
        @Test
        void get_all_products_success() throws Exception {
            // when
            MvcResult result = mockMvc.perform(get("/product/getAll")
                            .header("Authorization", "Bearer " + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            // then
            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).contains("Product list retrieved");
        }

        @Test
        void given_products_then_get_all_products_success() throws Exception {
            // given
            Product product1 = new Product("Computer", "Description 1", 100.0);
            Product product2 = new Product("Computer2", "Description 2", 200.0);

            productService.createProduct(product1);
            productService.createProduct(product2);

            // when
            MvcResult result = mockMvc.perform(get("/product/getAll")
                            .header("Authorization", "Bearer " + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            // then
            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).contains("Product list retrieved");
            assertThat(responseContent).contains("Computer");
            assertThat(responseContent).contains("Computer2");
        }
    }

    @Nested
    class UpdateProductTests {
        @Test
        void update_product_success() throws Exception {
            // given
            Product product1 = new Product("Computer", "Description 1", 100.0);
            Product createdProduct = productService.createProduct(product1);
            ProductRequestDTO productRequestDTO = new ProductRequestDTO("Computer", "Updated description", 300.0);

            // when
            MvcResult result = mockMvc.perform(put("/product/update/" + createdProduct.getId())
                            .header("Authorization", "Bearer " + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productRequestDTO)))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).contains("Updated description");
            assertThat(responseContent).contains("Computer");
            assertThat(responseContent).contains("300.0");
        }
    }

    @Nested
    class DeleteProductTests {

        @Test
        void delete_product_success() throws Exception {
            //given
            Product product1 = new Product("Computer", "Description 1", 100.0);
            productService.createProduct(product1);

            // when
            MvcResult result = mockMvc.perform(delete("/product/delete/1")
                            .header("Authorization", "Bearer " + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).contains("deleted product");
        }
    }


}
