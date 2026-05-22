package telecom.recharge.mobile_recharge_api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telecom.recharge.mobile_recharge_api.dto.RechargeRequestDTO;
import telecom.recharge.mobile_recharge_api.dto.RechargeResponseDTO;
import telecom.recharge.mobile_recharge_api.entity.RechargeRequest;
import telecom.recharge.mobile_recharge_api.entity.User;
import telecom.recharge.mobile_recharge_api.enums.OperatorType;
import telecom.recharge.mobile_recharge_api.enums.RechargeStatus;
import telecom.recharge.mobile_recharge_api.exception.InvalidStatusTransitionException;
import telecom.recharge.mobile_recharge_api.repository.RechargeRepository;
import telecom.recharge.mobile_recharge_api.service.RechargeService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RechargeServiceTest {

    @Mock
    private RechargeRepository repository;

    @InjectMocks
    private RechargeService service;

    private User createMockAdmin() {
        User admin = new User();
        admin.setUsername("adminUser");
        admin.setRole("ADMIN");
        return admin;
    }

    @Test
    void testValidSubmit() {
        User caller = createMockAdmin();
        RechargeRequestDTO dto = new RechargeRequestDTO();
        dto.setMsisdn("9876543210");
        dto.setAmount(new BigDecimal("199.00"));
        dto.setOperator(OperatorType.AIRTEL);

        RechargeRequest saved = new RechargeRequest();
        saved.setMsisdn("9876543210");
        saved.setAmount(new BigDecimal("199.00"));
        saved.setOperator(OperatorType.AIRTEL);
        saved.setStatus(RechargeStatus.PENDING);

        when(repository.save(any(RechargeRequest.class))).thenReturn(saved);

        RechargeResponseDTO result = service.submit(dto, caller);

        assertNotNull(result);
        assertEquals(RechargeStatus.PENDING, result.getStatus());
        verify(repository, times(1)).save(any(RechargeRequest.class));
    }

    @Test
    void testValidComplete() {
        User caller = createMockAdmin();
        RechargeRequest request = new RechargeRequest();
        request.setStatus(RechargeStatus.PENDING);

        when(repository.findById(1L)).thenReturn(Optional.of(request));
        when(repository.save(request)).thenReturn(request);

        RechargeResponseDTO result = service.complete(1L, caller);

        assertEquals(RechargeStatus.COMPLETED, result.getStatus());
    }

    @Test
    void testValidFail() {
        User caller = createMockAdmin();
        RechargeRequest request = new RechargeRequest();
        request.setStatus(RechargeStatus.PENDING);

        when(repository.findById(1L)).thenReturn(Optional.of(request));
        when(repository.save(request)).thenReturn(request);

        RechargeResponseDTO result = service.fail(1L, caller);

        assertEquals(RechargeStatus.FAILED, result.getStatus());
    }

    @Test
    void testInvalidTransition() {
        User caller = createMockAdmin();
        RechargeRequest request = new RechargeRequest();
        request.setStatus(RechargeStatus.COMPLETED);

        when(repository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(InvalidStatusTransitionException.class, () -> {
            service.complete(1L, caller);
        });
    }

    @Test
    void testRecordNotFound() {
        User caller = createMockAdmin();
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            service.complete(99L, caller);
        });
    }
}