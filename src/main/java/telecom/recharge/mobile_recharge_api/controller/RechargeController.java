package telecom.recharge.mobile_recharge_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import telecom.recharge.mobile_recharge_api.dto.RechargeRequestDTO;
import telecom.recharge.mobile_recharge_api.dto.RechargeResponseDTO;
import telecom.recharge.mobile_recharge_api.entity.User;
import telecom.recharge.mobile_recharge_api.enums.RechargeStatus;
import telecom.recharge.mobile_recharge_api.repository.UserRepository;
import telecom.recharge.mobile_recharge_api.service.RechargeService;

// Handles all recharge-related HTTP requests under /api/recharges
@Slf4j
@RestController
@RequestMapping("/api/recharges")
@RequiredArgsConstructor
public class RechargeController {

    private final RechargeService service;
    private final UserRepository userRepository;

    // ENDPOINT 1 — POST /api/recharges
    // Submits a new recharge request and returns 201 CREATED
    @PostMapping
    public ResponseEntity<RechargeResponseDTO> submit(@Valid @RequestBody RechargeRequestDTO dto) {
        User caller = getCurrentUser();
        log.info("POST /api/recharges — msisdn={} operator={} by user={}",
                dto.getMsisdn(), dto.getOperator(), caller.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.submit(dto, caller));
    }

    // ENDPOINT 2 — PATCH /api/recharges/{id}/complete
    // Marks a recharge request as COMPLETED by ID
    @PatchMapping("/{id}/complete")
    public ResponseEntity<RechargeResponseDTO> complete(@PathVariable Long id) {
        User caller = getCurrentUser();
        log.info("PATCH /api/recharges/{}/complete by admin={}", id, caller.getUsername());
        return ResponseEntity.ok(service.complete(id, caller));
    }

    // ENDPOINT 3 — PATCH /api/recharges/{id}/fail
    // Marks a recharge request as FAILED by ID
    @PatchMapping("/{id}/fail")
    public ResponseEntity<RechargeResponseDTO> fail(@PathVariable Long id) {
        User caller = getCurrentUser();
        log.info("PATCH /api/recharges/{}/fail by admin={}", id, caller.getUsername());
        return ResponseEntity.ok(service.fail(id, caller));
    }

    // ENDPOINT 4 — GET /api/recharges/{id}
    // Fetches a single recharge record by ID
    @GetMapping("/{id}")
    public ResponseEntity<RechargeResponseDTO> getById(@PathVariable Long id) {
        User caller = getCurrentUser();
        log.info("GET /api/recharges/{} contextUser={}", id, caller.getUsername());
        return ResponseEntity.ok(service.fetchById(id, caller));
    }

    // ENDPOINT 5 — GET /api/recharges or GET /api/recharges?msisdn=
    // Fetches all recharges, or filters by msisdn if provided
    @GetMapping
    public ResponseEntity<Page<RechargeResponseDTO>> getRequests(
            @RequestParam(required = false) String msisdn,
            @PageableDefault(size = 20) Pageable pageable) {
        User caller = getCurrentUser();
        if (msisdn != null && !msisdn.isBlank()) {
            log.info("GET /api/recharges?msisdn={} page={} user={}",
                    msisdn, pageable.getPageNumber(), caller.getUsername());
            return ResponseEntity.ok(service.fetchByMsisdn(msisdn, pageable, caller));
        }
        log.info("GET /api/recharges (All/Scoped) page={} user={}",
                pageable.getPageNumber(), caller.getUsername());
        return ResponseEntity.ok(service.fetchAllOrOperatorScoped(pageable, caller));
    }

    // ENDPOINT 6 — GET /api/recharges/status?status=
    // Fetches recharges filtered by status (PENDING / COMPLETED / FAILED)
    @GetMapping("/status")
    public ResponseEntity<Page<RechargeResponseDTO>> getByStatus(
            @RequestParam RechargeStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        User caller = getCurrentUser();
        log.info("GET /api/recharges/status?status={} page={} user={}",
                status, pageable.getPageNumber(), caller.getUsername());
        return ResponseEntity.ok(service.fetchByStatus(status, pageable, caller));
    }

    // Retrieves the currently logged-in user from the security context
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new SecurityException("Current authenticated context user record missing in DB."));
    }
}