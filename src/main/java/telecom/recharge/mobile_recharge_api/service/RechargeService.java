package telecom.recharge.mobile_recharge_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telecom.recharge.mobile_recharge_api.dto.RechargeRequestDTO;
import telecom.recharge.mobile_recharge_api.dto.RechargeResponseDTO;
import telecom.recharge.mobile_recharge_api.entity.RechargeRequest;
import telecom.recharge.mobile_recharge_api.entity.User;
import telecom.recharge.mobile_recharge_api.enums.OperatorType;
import telecom.recharge.mobile_recharge_api.enums.RechargeStatus;
import telecom.recharge.mobile_recharge_api.repository.RechargeRepository;
/*
                    all bussniess logic is there here


Dealer can only submit for their own operator✅
YesOnly ADMIN can mark COMPLETED or FAILED✅
YesDealer can only view their own operator's data✅
\YesScoped data fetch based on role✅
YesThrows error if request not found✅

 */

import java.util.Objects;

// Contains all business logic for recharge operations
@Slf4j
@Service
@RequiredArgsConstructor
public class RechargeService {

    private final RechargeRepository repository;

    // Creates a new recharge request — dealers can only submit for their own operator
    @Transactional
    public RechargeResponseDTO submit(RechargeRequestDTO dto, User caller) {
        if ("DEALER".equalsIgnoreCase(caller.getRole())) {
            OperatorType dealerOp = OperatorType.valueOf(caller.getOperator().toUpperCase());
            if (dto.getOperator() != dealerOp) {
                throw new SecurityException("Dealers are unauthorized to submit requests for other operators.");
            }
        }

        RechargeRequest request = new RechargeRequest();
        request.setMsisdn(dto.getMsisdn());
        request.setAmount(dto.getAmount());
        request.setOperator(dto.getOperator());
        request.setSubmittedBy(caller);

        RechargeRequest saved = repository.save(request);
        return mapToResponseDTO(saved);
    }

    // Marks a recharge as COMPLETED — only ADMIN can perform this
    @Transactional
    public RechargeResponseDTO complete(Long id, User caller) {
        validateAdmin(caller);
        RechargeRequest request = getRequestEntity(id);
        request.setStatus(RechargeStatus.COMPLETED);
        return mapToResponseDTO(repository.save(request));
    }

    // Marks a recharge as FAILED — only ADMIN can perform this
    @Transactional
    public RechargeResponseDTO fail(Long id, User caller) {
        validateAdmin(caller);
        RechargeRequest request = getRequestEntity(id);
        request.setStatus(RechargeStatus.FAILED);
        return mapToResponseDTO(repository.save(request));
    }

    // Fetches a single recharge by ID — dealers can only view their own operator's data
    @Transactional(readOnly = true)
    public RechargeResponseDTO fetchById(Long id, User caller) {
        RechargeRequest request = getRequestEntity(id);
        validateOwnership(request, caller);
        return mapToResponseDTO(request);
    }

    // Fetches recharges by mobile number — dealers are scoped to their operator only
    @Transactional(readOnly = true)
    public Page<RechargeResponseDTO> fetchByMsisdn(String msisdn, Pageable pageable, User caller) {
        if ("DEALER".equalsIgnoreCase(caller.getRole())) {
            OperatorType op = OperatorType.valueOf(caller.getOperator().toUpperCase());
            return repository.findByMsisdnAndOperator(msisdn, op, pageable).map(this::mapToResponseDTO);
        }
        return repository.findByMsisdn(msisdn, pageable).map(this::mapToResponseDTO);
    }

    // Fetches recharges by status — dealers are scoped to their operator only
    @Transactional(readOnly = true)
    public Page<RechargeResponseDTO> fetchByStatus(RechargeStatus status, Pageable pageable, User caller) {
        if ("DEALER".equalsIgnoreCase(caller.getRole())) {
            OperatorType op = OperatorType.valueOf(caller.getOperator().toUpperCase());
            return repository.findByStatusAndOperator(status, op, pageable).map(this::mapToResponseDTO);
        }
        return repository.findByStatus(status, pageable).map(this::mapToResponseDTO);
    }

    // Fetches all recharges — dealers see only their operator's data, admins see all
    @Transactional(readOnly = true)
    public Page<RechargeResponseDTO> fetchAllOrOperatorScoped(Pageable pageable, User caller) {
        if ("DEALER".equalsIgnoreCase(caller.getRole())) {
            OperatorType op = OperatorType.valueOf(caller.getOperator().toUpperCase());
            return repository.findByOperator(op, pageable).map(this::mapToResponseDTO);
        }
        return repository.findAll(pageable).map(this::mapToResponseDTO);
    }

    // Fetches recharge entity by ID — throws error if not found
    private RechargeRequest getRequestEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recharge request not found with ID: " + id));
    }

    // Checks if the caller is an ADMIN — throws error if not
    private void validateAdmin(User caller) {
        if (!"ADMIN".equalsIgnoreCase(caller.getRole())) {
            throw new SecurityException("Access Denied: Only Admins can modify transaction states.");
        }
    }

    // Ensures dealers can only access data belonging to their own operator
    private void validateOwnership(RechargeRequest request, User caller) {
        if ("DEALER".equalsIgnoreCase(caller.getRole())) {
            OperatorType dealerOp = OperatorType.valueOf(caller.getOperator().toUpperCase());
            if (request.getOperator() != dealerOp) {
                throw new SecurityException("Access Denied: Unauthorized to view alternate operator data.");
            }
        }
    }

    // Converts a RechargeRequest entity into a RechargeResponseDTO for the caller
    private RechargeResponseDTO mapToResponseDTO(RechargeRequest entity) {
        RechargeResponseDTO dto = new RechargeResponseDTO();
        dto.setId(entity.getId());
        dto.setMsisdn(entity.getMsisdn());
        dto.setAmount(entity.getAmount());
        dto.setOperator(entity.getOperator());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        if (entity.getSubmittedBy() != null) {
            dto.setSubmittedByUsername(entity.getSubmittedBy().getUsername());
        }
        return dto;
    }
}