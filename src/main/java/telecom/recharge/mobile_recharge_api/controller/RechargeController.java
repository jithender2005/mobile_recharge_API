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
import telecom.recharge.mobile_recharge_api.repository.UserRepository; // Essential to resolve getCurrentUser profiles
import telecom.recharge.mobile_recharge_api.service.RechargeService;

@Slf4j
@RestController
@RequestMapping("/api/recharges")
@RequiredArgsConstructor
public class RechargeController {

    private final RechargeService service;
    private final UserRepository userRepository; // Added to turn Principal into a full User Entity

    @PostMapping
    public ResponseEntity<RechargeResponseDTO> submit(@Valid @RequestBody RechargeRequestDTO dto) {
        User caller = getCurrentUser();
        log.info("POST /api/recharges — msisdn={} operator={} by user={}", dto.getMsisdn(), dto.getOperator(), caller.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.submit(dto, caller));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<RechargeResponseDTO> complete(@PathVariable Long id) {
        User caller = getCurrentUser();
        log.info("PATCH /api/recharges/{}/complete by admin={}", id, caller.getUsername());
        return ResponseEntity.ok(service.complete(id, caller));
    }

    @PatchMapping("/{id}/fail")
    public ResponseEntity<RechargeResponseDTO> fail(@PathVariable Long id) {
        User caller = getCurrentUser();
        log.info("PATCH /api/recharges/{}/fail by admin={}", id, caller.getUsername());
        return ResponseEntity.ok(service.fail(id, caller));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RechargeResponseDTO> getById(@PathVariable Long id) {
        User caller = getCurrentUser();
        log.info("GET /api/recharges/{} contextUser={}", id, caller.getUsername());
        return ResponseEntity.ok(service.fetchById(id, caller));
    }

    @GetMapping
    public ResponseEntity<Page<RechargeResponseDTO>> getRequests(
            @RequestParam(required = false) String msisdn,
            @PageableDefault(size = 20) Pageable pageable) {
        User caller = getCurrentUser();
        if (msisdn != null && !msisdn.isBlank()) {
            log.info("GET /api/recharges?msisdn={} page={} user={}", msisdn, pageable.getPageNumber(), caller.getUsername());
            return ResponseEntity.ok(service.fetchByMsisdn(msisdn, pageable, caller));
        }
        log.info("GET /api/recharges (All/Scoped) page={} user={}", pageable.getPageNumber(), caller.getUsername());
        return ResponseEntity.ok(service.fetchAllOrOperatorScoped(pageable, caller));
    }

    @GetMapping("/status")
    public ResponseEntity<Page<RechargeResponseDTO>> getByStatus(
            @RequestParam RechargeStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        User caller = getCurrentUser();
        log.info("GET /api/recharges/status?status={} page={} user={}", status, pageable.getPageNumber(), caller.getUsername());
        return ResponseEntity.ok(service.fetchByStatus(status, pageable, caller));
    }

    // --- Private Helper to pull Authenticated user safely ---
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new SecurityException("Current authenticated context user record missing in DB."));
    }
}