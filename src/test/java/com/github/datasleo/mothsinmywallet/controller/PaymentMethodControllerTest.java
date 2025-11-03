package com.github.datasleo.mothsinmywallet.controller;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mockito;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.github.datasleo.mothsinmywallet.config.SecurityConfig;
import com.github.datasleo.mothsinmywallet.dto.PaymentMethodDto;
import com.github.datasleo.mothsinmywallet.exception.PaymentMethodAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.exception.PaymentMethodNotFoundOrNotAuthorizedException;
import com.github.datasleo.mothsinmywallet.exception.UnauthorizedAccountException;
import com.github.datasleo.mothsinmywallet.model.PaymentMethod;
import com.github.datasleo.mothsinmywallet.service.AccountService;
import com.github.datasleo.mothsinmywallet.service.PaymentMethodService;

@WebMvcTest(PaymentMethodController.class)
@Import({SecurityConfig.class, PaymentMethodControllerTest.TestConfig.class})
@AutoConfigureMockMvc(addFilters=true)
public class PaymentMethodControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private PaymentMethodService paymentService;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    public void setup() {
        Mockito.reset(paymentService);
        Mockito.reset(accountService);
    }

// ----------------------------------------------- GET /payment-method/ -----------------------------------------------

    @Test
    public void WhenGetPaymentMethodPageButUserIsNotAuthenticated_ThenRedirectToLoginPage() throws Exception {

        // ACT & ASSERT
        mockMvc
            .perform(get("/payment-method"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));

    }

    @Test
    @WithMockUser
    public void WhenGetPaymentMethodPage_ThenReturnTheViewPaymentMethodMan() throws Exception {

        // ACT & ASSERT
        mockMvc
            .perform(get("/payment-method"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_man"));

    }

    @Test
    @WithMockUser
    public void WhenGetPaymentMethodPageWithModel_TheReturnViewPaymentsMethodAndModel() throws Exception {

        // ARRANGE
        List<PaymentMethod> listOfPayment = List.of(
            new PaymentMethod("credit card", "credit card description", null),
            new PaymentMethod("cash", "cash description", null)
        );

        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(1L);
        when(paymentService.getAllPaymentsByAccountId(1L)).thenReturn(listOfPayment);


        // ACT & ASSERT
        mockMvc
            .perform(get("/payment-method"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_man"))
            .andExpect(model().attributeExists("payments_method"))
            .andExpect(model().attribute("payments_method", hasSize(2)))
            .andExpect(model().attribute("payments_method", hasItem(
                hasProperty("paymentName", is("credit card"))
            )))
            .andExpect(model().attribute("payments_method", hasItem(
                hasProperty("paymentName", is("cash"))
            )));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(paymentService, times(1)).getAllPaymentsByAccountId(anyLong());

    }

// ----------------------------------------------- GET /payment-method/add -----------------------------------------------

    @Test
    public void WhenGetPaymentMethodAddPageButUserIsNotAuthenticated_TheRedirectToLoginPage() throws Exception {

        // ACT & ASSERT
        mockMvc
            .perform(get("/payment-method/add"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));

    }

    @Test
    @WithMockUser
    public void WhenGetPaymentMethodAddPage_ThenReturnViewPaymentsMethodAdd() throws Exception {

        // ACT & ASSERT
        mockMvc
            .perform(get("/payment-method/add"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_add"));

    }

// ----------------------------------------------- POST /payment-method/add -----------------------------------------------

    @Test
    public void WhenPostPaymentMethodAddPageButIsNotAuthenticated_ThenRedirectToLoginPage() throws Exception {

        // ACT & ASSERT
        mockMvc
            .perform(post("/payment-method/add")
                .with(csrf())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));

    }

    @Test
    @WithMockUser
    public void WhenPostPaymentMethodAddPageButUnauthorizedAccount_ThenReturnModelWithError() throws Exception {

        // ARRANGE
        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(1L);
        
        doThrow(new UnauthorizedAccountException("Unauthorized account."))
            .when(paymentService).createPayment(any(PaymentMethodDto.class)); 

            
        // ACT & ASSERT
        mockMvc
            .perform(post("/payment-method/add")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("paymentName", "credit card")
                .param("paymentDescription", "description")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_add"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Unauthorized account."));


        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(paymentService, times(1)).createPayment(any(PaymentMethodDto.class));

    }

    @Test
    @WithMockUser
    public void WhenPostPaymentMethodAddPageButPaymentAlreadyExists_ThenReturnModelWithError() throws Exception {

        // ARRANGE
        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(1L);
        
        doThrow(new PaymentMethodAlreadyExistsException("Payment method 'credit card' already exists."))
            .when(paymentService).createPayment(any(PaymentMethodDto.class));


        // ACT & ASSERT
        mockMvc
            .perform(post("/payment-method/add")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("paymentName", "credit card")
                .param("paymentDescription", "description")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_add"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Payment method 'credit card' already exists."));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(paymentService, times(1)).createPayment(any(PaymentMethodDto.class));

    }

    @Test
    @WithMockUser
    public void WhenPostPaymentMethodAddPage_ThenReturnModelWithSuccess() throws Exception {

        // ARRANGE
        PaymentMethod mockPayment = new PaymentMethod("credit card", "description", null);
        
        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(1L);
        when(paymentService.createPayment(any(PaymentMethodDto.class))).thenReturn(mockPayment);


        // ACT & ASSERT
        mockMvc
            .perform(post("/payment-method/add")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("paymentName", "credit card")
                .param("paymentDescription", "description")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_add"))
            .andExpect(model().attributeExists("success"))
            .andExpect(model().attribute("success", "Payment method 'credit card' created with success."));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(paymentService, times(1)).createPayment(any(PaymentMethodDto.class));

    }

// ----------------------------------------------- GET /payment-method/edit/{id} -----------------------------------------------

    @Test
    public void WhenGetPaymentMethodEditPageButUserIsNotAuthenticated_ThenRedirectToLoginPage() throws Exception {

        // ACT & ASSERT
        mockMvc
            .perform(get("/payment-method/edit/1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));

    }

    @Test
    @WithMockUser
    public void WhenGetPaymentMethodPageButUnauthorizedAccount_ThenReturnModelWithError() throws Exception {

        // ARRANGE
        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(1L);

        doThrow(new UnauthorizedAccountException("Unauthorized account."))
            .when(paymentService).getOnePaymentMethod(anyLong(), anyLong());


        // ACT & ASSERT
        mockMvc
            .perform(get("/payment-method/edit/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_edit"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Unauthorized account."));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(paymentService, times(1)).getOnePaymentMethod(anyLong(), anyLong());

    }

    @Test
    @WithMockUser
    public void WhenGetPaymentMethodEditPageButPaymentNotFoundOrNotAuthorized_ThenReturnModelWithError() throws Exception {

        // ARRANGE
        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(1L);

        doThrow(new PaymentMethodNotFoundOrNotAuthorizedException("Payment method not found or not authorized."))
            .when(paymentService).getOnePaymentMethod(anyLong(), anyLong());

        
        // ACT & ASSERT
        mockMvc
            .perform(get("/payment-method/edit/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_edit"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Payment method not found or not authorized."));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(paymentService, times(1)).getOnePaymentMethod(anyLong(), anyLong());

    }

    @Test
    @WithMockUser
    public void WhenGetPaymentMethodEditPage_ThenReturnModelWithPaymentNameAndPaymentDescription() throws Exception {

        // ARRANGE
        PaymentMethod mockPayment = new PaymentMethod("credit card", "description", null);

        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(1L);
        when(paymentService.getOnePaymentMethod(anyLong(), anyLong())).thenReturn(mockPayment);


        // ACT & ASSERT
        mockMvc
            .perform(get("/payment-method/edit/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_edit"))
            .andExpect(model().attributeExists("id"))
            .andExpect(model().attributeExists("paymentName"))
            .andExpect(model().attributeExists("paymentDescription"))
            .andExpect(model().attribute("id", 1L))
            .andExpect(model().attribute("paymentName", "credit card"))
            .andExpect(model().attribute("paymentDescription", "description"));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(paymentService, times(1)).getOnePaymentMethod(anyLong(), anyLong());

    }

// ----------------------------------------------- PATCH /payment-method/edit/{id} -----------------------------------------------

    @Test
    public void WhenPatchPaymentMethodEditButUserIsNotAuthenticated_ThenRedirectToLogin() throws Exception {

        // ACT & ASSERT
        mockMvc
            .perform(patch("/payment-method/edit/1")
                .with(csrf())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));

    }

    @Test
    @WithMockUser
    public void WhenPatchPaymentMethodEditPageButUnauthorizedAccount_ThenReturnModelWithError() throws Exception {

        // ARRANGE
        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(1L);

        doThrow(new UnauthorizedAccountException("Unauthorized account."))
            .when(paymentService).updatePayment(eq(1L), eq(1L), any(PaymentMethodDto.class));


        // ACT & ASSERT
        mockMvc
            .perform(patch("/payment-method/edit/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("paymentName", "credit card")
                .param("paymentDescription", "description")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_man"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Unauthorized account."));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(paymentService, times(1)).updatePayment(eq(1L), eq(1L), any(PaymentMethodDto.class));

    }

    @Test
    @WithMockUser
    public void WhenPatchPaymentMethodEditPageButPaymentNotFoundOrNotAuthorized_ThenReturnModelWithError() throws Exception {

        // ARRANGE
        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(1L);

        doThrow(new PaymentMethodNotFoundOrNotAuthorizedException("Payment method not found or not authorized."))
            .when(paymentService).updatePayment(anyLong(), anyLong(), any(PaymentMethodDto.class));
        

        // ACT & ASSERT
        mockMvc
            .perform(patch("/payment-method/edit/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("paymentName", "credit card")
                .param("paymentDescription", "description")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_man"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Payment method not found or not authorized."));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(paymentService, times(1)).updatePayment(eq(1L), eq(1L), any(PaymentMethodDto.class));

    }   

    @Test
    @WithMockUser
    public void WhenPatchPaymentMethodEditPageButPaymentAlreadyExists_ThenReturnModelWithError() throws Exception {

        // ARRANGE
        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(1L);

        doThrow(new PaymentMethodAlreadyExistsException("Payment method 'credit card' already exists."))
            .when(paymentService).updatePayment(anyLong(), anyLong(), any(PaymentMethodDto.class));
        

        // ACT & ASSERT
        mockMvc
            .perform(patch("/payment-method/edit/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("paymentName", "credit card")
                .param("paymentDescription", "description")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_man"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Payment method 'credit card' already exists."));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(paymentService, times(1)).updatePayment(eq(1L), eq(1L), any(PaymentMethodDto.class));

    }  

    @Test
    @WithMockUser
    public void WhenPatchPaymentMethodEditPage_ThenReturnModelWithSuccess() throws Exception {

        // ARRANGE
        PaymentMethod mockPayment = new PaymentMethod("credit card", "description", null);

        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(1L);
        when(paymentService.updatePayment(anyLong(), anyLong(), any(PaymentMethodDto.class))).thenReturn(mockPayment);


        // ACT & ASSERT
        mockMvc
            .perform(patch("/payment-method/edit/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("paymentName", "credit card")
                .param("paymentDescription", "description")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_man"))
            .andExpect(model().attributeExists("success"))
            .andExpect(model().attribute("success", "Payment method 'credit card' was updated with success"));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(paymentService, times(1)).updatePayment(anyLong(), anyLong(), any(PaymentMethodDto.class));

    }

// ----------------------------------------------- DELETE /payment-method/delete/{id} -----------------------------------------------

    @Test
    public void WhenDeletePaymentMethodPageButUserIsNotAuthenticated_ThenRedirectToLogin() throws Exception {

        // ACT & ASSERT
        mockMvc
            .perform(delete("/payment-method/delete/1")
                .with(csrf())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));

    }

    @Test
    @WithMockUser
    public void WhenDeletePaymentMethodPageButUnauthorizedAccount_ThenReturnModelWithError() throws Exception {

        // ARRANGE
        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(1l);

        doThrow(new UnauthorizedAccountException("Unauthorized account."))
            .when(paymentService).deletePayment(anyLong(), anyLong());


        // ACT & ASSERT
        mockMvc
            .perform(delete("/payment-method/delete/1")
                .with(csrf())
            )
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_man"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Unauthorized account."));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(paymentService, times(1)).deletePayment(anyLong(), anyLong());

    }

    @Test
    @WithMockUser
    public void WhenDeletePaymentMethodPageButPaymentNotFoundOrNotAuthorized_ThenReturnModelWithError() throws Exception {

        // ARRANGE
        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(1L);

        doThrow(new PaymentMethodNotFoundOrNotAuthorizedException("Payment method not found or not authorized."))
            .when(paymentService).deletePayment(anyLong(), anyLong());


        // ACT & ASSERT
        mockMvc
            .perform(delete("/payment-method/delete/1")
                .with(csrf())
            )
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_man"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Payment method not found or not authorized."));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(paymentService, times(1)).deletePayment(anyLong(), anyLong());

    }

    @Test
    @WithMockUser
    public void WhenDeletePaymentMethodPage_ThenReturnModelWithSuccess() throws Exception {
        
        // ARRANGE
        PaymentMethod mockPayment = new PaymentMethod("credit card", "description", null);

        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(1L);
        when(paymentService.deletePayment(anyLong(), anyLong())).thenReturn(mockPayment);
        

        // ACT & ASSERT
        mockMvc
            .perform(delete("/payment-method/delete/1")
                .with(csrf())
            )
            .andExpect(status().isOk())
            .andExpect(view().name("auth/payment_method_man"))
            .andExpect(model().attributeExists("success"))
            .andExpect(model().attribute("success", "Payment method 'credit card' was deleted with success."));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(paymentService, times(1)).deletePayment(anyLong(), anyLong());
    
    }

// ----------------------------------------------- TestConfiguration Class -----------------------------------------------

    @TestConfiguration
    static class TestConfig {

        @Bean
        public PaymentMethodService paymentService() {
            return Mockito.mock(PaymentMethodService.class);
        }

        @Bean
        public AccountService accountService() {
            return Mockito.mock(AccountService.class);
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return username -> User
                .withUsername(username)
                .password("hashed_password")
                .roles("USER")
                .build();
        }

    }

}