package telecom.recharge.mobile_recharge_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telecom.recharge.mobile_recharge_api.dto.AuthRequest;
import telecom.recharge.mobile_recharge_api.dto.AuthResponse;
import telecom.recharge.mobile_recharge_api.service.UserService;

//                  auth controller get the request from the login page

// Handles all authentication related HTTP requests under /api/auth
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // Registers a new user and returns a JWT token
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.signup(request));
    }

    // Authenticates an existing user and returns a JWT token
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}