package com.github.datasleo.mothsinmywallet.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.datasleo.mothsinmywallet.dto.PaymentMethodDto;
import com.github.datasleo.mothsinmywallet.exception.PaymentMethodAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.exception.PaymentMethodNotFoundOrNotAuthorizedException;
import com.github.datasleo.mothsinmywallet.exception.UnauthorizedAccountException;
import com.github.datasleo.mothsinmywallet.model.Account;
import com.github.datasleo.mothsinmywallet.model.PaymentMethod;
import com.github.datasleo.mothsinmywallet.repository.AccountRepository;
import com.github.datasleo.mothsinmywallet.repository.PaymentMethodRepository;

@ExtendWith(MockitoExtension.class)
public class PaymentMethodServiceTest {
    
    @Mock
    private PaymentMethodRepository paymentRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private PaymentMethodService paymentService;

    private PaymentMethodDto validPaymentDto;

    @BeforeEach
    public void Setup() {
        validPaymentDto = new PaymentMethodDto("credit card", "description", 1L);
    }

// -------------------------------------------------- createAccount method -------------------------------------------------- 

    @Test
    public void WhenCreatePaymentButAccountIdWasNotFound_ThenThrowUnauthorizedAccountException() throws Exception {

        // ARRANGE
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());
        

        // ACT & ASSERT 
        UnauthorizedAccountException thrown = assertThrows(
            UnauthorizedAccountException.class,
            () -> paymentService.createPayment(validPaymentDto)  
        );

        assertEquals("Unauthorized account.", thrown.getMessage());

        verify(accountRepository, times(1)).findById(anyLong());

    }

    @Test
    public void WhenCreatePaymentButRepeated_ThenThrowPaymentAlreadyExistsException() throws Exception {

        // ARRANGE
        Account mockAccount = new Account("foo@foo.com", "hashed_password", "foo");
        mockAccount.setId(1L);

        when(accountRepository.findById(eq(mockAccount.getId()))).thenReturn(Optional.of(mockAccount));
        when(paymentRepository.findByPaymentNameAndAccountId(anyString(), eq(mockAccount.getId()))).thenReturn(new PaymentMethod());


        // ACT & ASSERT
        PaymentMethodAlreadyExistsException thrown = assertThrows(
            PaymentMethodAlreadyExistsException.class,
            () -> paymentService.createPayment(validPaymentDto)
        );

        assertEquals("Payment method 'credit card' already exists.", thrown.getMessage());

        verify(accountRepository, times(1)).findById(eq(mockAccount.getId()));
        verify(paymentRepository, times(1)).findByPaymentNameAndAccountId(anyString(), anyLong());

    }

    @Test
    public void WhenCreatePayment_ThenReturnPayment() throws Exception {

        // ARRANGE
        Account mockAccount = new Account("foo@foo.com", "hashed_password", "foo");
        mockAccount.setId(1L);

        when(accountRepository.findById(eq(mockAccount.getId()))).thenReturn(Optional.of(mockAccount));
        when(paymentRepository.findByPaymentNameAndAccountId(anyString(), anyLong())).thenReturn(null);

        when(paymentRepository.save(any(PaymentMethod.class))).thenAnswer(answer -> {
            PaymentMethod payment = answer.getArgument(0);

            payment.setId(1L);

            return payment;
        });


        // ACT
        PaymentMethod result = paymentService.createPayment(validPaymentDto);


        // ASSERT
        assertNotNull(result);
        assertEquals("credit card", result.getPaymentName());
        assertEquals("description", result.getPaymentDescription());
        assertEquals(1L, result.getAccount().getId());

        verify(accountRepository, times(1)).findById(eq(mockAccount.getId()));
        verify(paymentRepository, times(1)).findByPaymentNameAndAccountId(anyString(), anyLong());
        verify(paymentRepository, times(1)).save(any(PaymentMethod.class));

    }

// -------------------------------------------------- getAllPaymentsByAccountId method -------------------------------------------------- 


    @Test
    public void WhenGetAllPaymentsByAccountIdButAccountIdWasNotFound_ThenThrowUnauthorizedAccountException() throws Exception {

        // ARRANGE
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());


        // ACT & ASSERT
        UnauthorizedAccountException thrown = assertThrows(
            UnauthorizedAccountException.class,
            () -> paymentService.getAllPaymentsByAccountId(anyLong())
        );

        assertEquals("Unauthorized account.", thrown.getMessage());

        verify(accountRepository, times(1)).findById(anyLong());

    }

    @Test
    public void WhenGetAllPaymentsByAccountId_ThenReturnListOfPayments() throws Exception {

        // ARRANGE
        Account mockAccount = new Account("foo@foo.com", "hashed_password", "foo");
        mockAccount.setId(1L);

        List<PaymentMethod> mockListOfPayments = List.of(
            new PaymentMethod("credit card", "credit card description", mockAccount),
            new PaymentMethod("cash", "cash description", mockAccount)
        );

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(paymentRepository.findAllByAccountId(eq(mockAccount.getId()))).thenReturn(mockListOfPayments);


        // ACT
        List<PaymentMethod> result = paymentService.getAllPaymentsByAccountId(mockAccount.getId());


        // ASSERT
        assertEquals(result.size(), 2);

        assertEquals(result.get(0).getPaymentName(), "credit card");
        assertEquals(result.get(0).getPaymentDescription(), "credit card description");

        assertEquals(result.get(1).getPaymentName(), "cash");
        assertEquals(result.get(1).getPaymentDescription(), "cash description");

    }

// -------------------------------------------------- getOnePaymentMethod method -------------------------------------------------- 

    @Test
    public void WhenGetOnePaymentMethodButAccountIdWasNotFound_ThenThrowUnauthorizedAccountException() throws Exception {

        // ARRANGE 
        long mockPaymentId = 1L;
        long mockAccountId = 1l;

        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());


        // ACT & ASSERT
        UnauthorizedAccountException thrown = assertThrows(
            UnauthorizedAccountException.class,
            () -> paymentService.getOnePaymentMethod(mockPaymentId, mockAccountId)
        );

        
        assertEquals("Unauthorized account.", thrown.getMessage());

        verify(accountRepository, times(1)).findById(anyLong());

    }

    @Test
    public void WhenGetOnePaymentMethodButPaymentNameWasNotFound_ThenThrowPaymentNameWasNotFoundException() throws Exception {

        // ARRANGE 
        long mockPaymentId = 1L;
        long mockAccountId = 1l;

        Account mockAccount = new Account("foo@foo.com", "hashed_passowrd", "foo");

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(paymentRepository.findOneByIdAndAccountId(anyLong(), anyLong())).thenReturn(null);


        // ACT & ASSERT
        PaymentMethodNotFoundOrNotAuthorizedException thrown = assertThrows(
            PaymentMethodNotFoundOrNotAuthorizedException.class,
            () -> paymentService.getOnePaymentMethod(mockPaymentId, mockAccountId)
        );

        
        assertEquals("Payment method not found or not authorized.", thrown.getMessage());

        verify(accountRepository, times(1)).findById(anyLong());
        verify(paymentRepository, times(1)).findOneByIdAndAccountId(anyLong(), anyLong());

    }

    @Test
    public void WhenGetOnePaymentMethod_ThenReturnOnePaymentMethod() throws Exception {
        
        // ARRANGE 
        long mockPaymentId = 1L;
        long mockAccountId = 1l;

        Account mockAccount = new Account("foo@foo.com", "hashed_passowrd", "foo");
        PaymentMethod mockPayment = new PaymentMethod("credit card", "description", mockAccount);

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(paymentRepository.findOneByIdAndAccountId(anyLong(), anyLong())).thenReturn(mockPayment);


        // ACT & ASSERT
        PaymentMethod result = paymentService.getOnePaymentMethod(mockPaymentId, mockAccountId);

        assertEquals("credit card", result.getPaymentName());
        assertEquals("description", result.getPaymentDescription());

        verify(accountRepository, times(1)).findById(anyLong());
        verify(paymentRepository, times(1)).findOneByIdAndAccountId(anyLong(), anyLong());

    }

// -------------------------------------------------- updatePayment method -------------------------------------------------- 

    @Test
    public void WhenUpdatePaymentButAccountIdWasNotFound_ThenThrowUnauthorizedAccountException() throws Exception {

        // ARRANGE 
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());


        // ACT & ASSERT
        UnauthorizedAccountException thrown = assertThrows(
            UnauthorizedAccountException.class,
            () -> paymentService.updatePayment(1L, 1L, validPaymentDto)
        );


        assertEquals("Unauthorized account.", thrown.getMessage());

        verify(accountRepository, times(1)).findById(anyLong());

    }

    @Test
    public void WhenUpdatePaymentButPaymentNotFoundOrNotAuthorized_ThenThrowPaymentNotFoundOrNotAuthorized() throws Exception {

        // ARRANGE
        Account mockAccount = new Account("foo@foo.com", "hashed_password", "foo");
        mockAccount.setId(1L);

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(paymentRepository.findOneByIdAndAccountId(anyLong(), anyLong())).thenReturn(null);


        // ACT & ASSERT
        PaymentMethodNotFoundOrNotAuthorizedException thrown = assertThrows(
            PaymentMethodNotFoundOrNotAuthorizedException.class,
            () -> paymentService.updatePayment(1L, 1L, validPaymentDto)
        );

        assertEquals("Payment method not found or not authorized.", thrown.getMessage());

        verify(accountRepository, times(1)).findById(eq(mockAccount.getId()));
        verify(paymentRepository, times(1)).findOneByIdAndAccountId(anyLong(), anyLong());

    } 

    @Test
    public void WhenUpdatePaymentButPaymentAlreadyExists_ThenThrowPaymentAlreadyExistsException() throws Exception {

        // ARRANGE
        Account mockAccount = new Account("foo@foo.com", "hashed_password", "foo");
        long mockAccountId = 1L;
        mockAccount.setId(mockAccountId);
        
        PaymentMethod mockPayment = new PaymentMethod("credit card", "description", mockAccount);
        PaymentMethod mockPayment4Return = new PaymentMethod("credits card", "description", mockAccount);

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(paymentRepository.findOneByIdAndAccountId(anyLong(), anyLong())).thenReturn(mockPayment); 
        when(paymentRepository.findByPaymentNameAndAccountId(anyString(), anyLong())).thenReturn(mockPayment4Return);


        // ACT & ASSERT
        PaymentMethodAlreadyExistsException thrown = assertThrows(
            PaymentMethodAlreadyExistsException.class,
            () -> paymentService.updatePayment(1L, 1L, validPaymentDto)
        );

        assertEquals("Payment method 'credit card' already exists.", thrown.getMessage());

        verify(accountRepository, times(1)).findById(eq(mockAccount.getId()));
        verify(paymentRepository, times(1)).findOneByIdAndAccountId(anyLong(), anyLong());
        verify(paymentRepository, times(1)).findByPaymentNameAndAccountId(anyString(), anyLong());

    }

    @Test
    public void WhenUpdatePayment_ThenReturnPaymentUpdated() throws Exception {

        // ARRANGE
        Account mockAccount = new Account("foo@foo.com", "hashed_password", "foo");
        long mockAccountId = 1L;
        mockAccount.setId(mockAccountId);
        
        PaymentMethod mockPayment = new PaymentMethod("credit card", "description", mockAccount);
        PaymentMethodDto mockPaymentDto = new PaymentMethodDto("new payment", "new description", 1L);

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(paymentRepository.findOneByIdAndAccountId(anyLong(), anyLong())).thenReturn(mockPayment); 
        when(paymentRepository.findByPaymentNameAndAccountId(anyString(), anyLong())).thenReturn(null);
        when(paymentRepository.save(any(PaymentMethod.class))).thenAnswer(answer -> answer.getArgument(0)); 


        // ACT
        PaymentMethod result = paymentService.updatePayment(1L, 1L, mockPaymentDto);


        // ASSERT
        assertEquals("new payment", result.getPaymentName());
        assertEquals("new description", result.getPaymentDescription());

        verify(accountRepository, times(1)).findById(eq(mockAccount.getId()));
        verify(paymentRepository, times(1)).findOneByIdAndAccountId(anyLong(), anyLong());
        verify(paymentRepository, times(1)).findByPaymentNameAndAccountId(anyString(), anyLong());
        verify(paymentRepository, times(1)).save(any(PaymentMethod.class));

    }

// -------------------------------------------------- deletePayment method -------------------------------------------------- 

    @Test
    public void WhenDeletePaymentButAccountIdWasNotFound_ThenThrowUnauthorizedAccountException() throws Exception {

        // ARRANGE
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());


        // ACT & ASSERT
        UnauthorizedAccountException thrown = assertThrows(
            UnauthorizedAccountException.class,
            () -> paymentService.deletePayment(1L, 1L)
        );

        assertEquals("Unauthorized account.", thrown.getMessage());

        verify(accountRepository, times(1)).findById(anyLong());

    }


    @Test
    public void WhenDeletePaymentButPaymentIdWasNotFound_ThenThrowPaymentNotFoundOrNotAuthorizedException() throws Exception {

        // ARRANGE
        Account mockAccount = new Account("foo@foo.com", "hashed_password", "foo");
        mockAccount.setId(1L);

        when(accountRepository.findById(eq(1L))).thenReturn(Optional.of(mockAccount));
        when(paymentRepository.findOneByIdAndAccountId(eq(1L), eq(1L))).thenReturn(null);

    
        // ACT & ASSERT
        PaymentMethodNotFoundOrNotAuthorizedException thrown = assertThrows(
            PaymentMethodNotFoundOrNotAuthorizedException.class,
            () -> paymentService.deletePayment(1L, 1L)
        );

        assertEquals("Payment method not found or not authorized.", thrown.getMessage());

        verify(accountRepository, times(1)).findById(eq(1L));
        verify(paymentRepository, times(1)).findOneByIdAndAccountId(eq(1L), eq(1L));

    }


    @Test
    public void WhenDeletPayment_ThenDeletePayment() throws Exception {

        // ARRANGE
        Account mockAccount = new Account("foo@foo.com", "hashed_password", "foo");
        mockAccount.setId(1L);

        PaymentMethod mockPayment = new PaymentMethod("credit card", "description", mockAccount);

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(paymentRepository.findOneByIdAndAccountId(anyLong(), anyLong())).thenReturn(mockPayment);


        // ACT
        PaymentMethod result = paymentService.deletePayment(1L, 1L);

        
        // ASSERT
        assertEquals("credit card", result.getPaymentName());
        assertEquals("description", result.getPaymentDescription());
        assertEquals(1L, result.getAccount().getId());

        verify(accountRepository, times(1)).findById(anyLong());
        verify(paymentRepository, times(1)).findOneByIdAndAccountId(anyLong(), anyLong());
    
    }
    
}
