package com.example.ecommerceapp;

import com.example.ecommerceapp.model.dto.*;
import com.example.ecommerceapp.repositories.InvoiceRepository;
import com.example.ecommerceapp.repositories.UserRepository;
import com.example.ecommerceapp.service.InvoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
@AutoConfigureMockMvc
public class InvoiceControllerTest {

    @Autowired
    CacheManager cacheManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    InvoiceService invoiceService;


    @Value("${app.credit-limit}")
    private int creditLimit;

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
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        loginForTest();
    }

    @AfterEach
    void tearDown() throws Exception {
        invoiceRepository.deleteAll();
        userRepository.deleteAll();
    }

    private UserDTO getUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("john.doe@example.com");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        return userDTO;
    }

    @Nested
    class SubmitInvoiceTests {
        @Test
        void submit_invoice_success() throws Exception {
            // given
            BaseInvoiceDTO request = new BaseInvoiceDTO();
            request.setAmount(150.0);
            request.setProductName("Product A");
            request.setBillNo("INV001");
            request.setUser(getUserDTO());
            // when & then
            MvcResult result = mockMvc.perform(post("/invoices/submit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwtToken)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).contains("Invoice accepted.");
        }

        @Test
        void submit_invoice_failure() throws Exception {
            // given
            BaseInvoiceDTO request = new BaseInvoiceDTO();
            request.setAmount(creditLimit + 100.0);
            request.setProductName("Product B");
            request.setBillNo("INV002");
            request.setUser(getUserDTO());
            // when & then
            MvcResult result = mockMvc.perform(post("/invoices/submit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwtToken)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).contains("Invoice rejected.");
        }
    }


    @Nested
    class GetApprovedInvoicesTests {
        @Test
        void get_approved_invoices_success() throws Exception {
            // given
            InvoicedApprovedDTO approvedInvoice = new InvoicedApprovedDTO();
            approvedInvoice.setAmount(10.0);
            approvedInvoice.setProductName("Product A");
            approvedInvoice.setBillNo("INV001");
            approvedInvoice.setUser(getUserDTO());
            invoiceService.processInvoice(approvedInvoice);


            InvoicedApprovedDTO approvedInvoice2 = new InvoicedApprovedDTO();
            approvedInvoice2.setAmount(10.0);
            approvedInvoice2.setProductName("Product B");
            approvedInvoice2.setBillNo("INV002");
            approvedInvoice2.setUser(getUserDTO());
            invoiceService.processInvoice(approvedInvoice2);


            InvoicedApprovedDTO approvedInvoice3 = new InvoicedApprovedDTO();
            approvedInvoice3.setAmount(100.0);
            approvedInvoice3.setProductName("Product C");
            approvedInvoice3.setBillNo("INV003");
            approvedInvoice3.setUser(getUserDTO());
            invoiceService.processInvoice(approvedInvoice3);

            // when & then
            MvcResult result = mockMvc.perform(get("/invoices/approved")
                            .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).contains("Product A");
            assertThat(responseContent).contains("Product B");
            assertThat(responseContent).contains("Product C");
        }
    }

    @Nested
    class GetRejectedInvoicesTests {
        @Test
        void get_rejected_invoices_success() throws Exception {
            // given
            InvoiceRejectedDTO rejectedInvoice = new InvoiceRejectedDTO();
            rejectedInvoice.setAmount(creditLimit + 300.0);
            rejectedInvoice.setProductName("Product B");
            rejectedInvoice.setBillNo("INV002");
            rejectedInvoice.setUser(getUserDTO());

            //when
            invoiceService.processInvoice(rejectedInvoice);

            //  then
            MvcResult result = mockMvc.perform(get("/invoices/rejected")
                            .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            assertThat(responseContent).contains("Product B");
        }
    }


}
