package telecom.recharge.mobile_recharge_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import telecom.recharge.mobile_recharge_api.config.JwtUtil;
import telecom.recharge.mobile_recharge_api.dto.AuthRequest;
import telecom.recharge.mobile_recharge_api.dto.AuthResponse;
import telecom.recharge.mobile_recharge_api.entity.User;
import telecom.recharge.mobile_recharge_api.repository.UserRepository;


//                      UserService      runs the logic -->user repository
/*
* UserService  →  runs the logic
  └── UserRepository  finds your user from DB
  └── User entity     the user object from DB
  └── BCrypt          checks your password against the hash
  └── JwtUtil         generates a 10-hour JWT token
  └── AuthResponse    packages token + role + operator*/


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthResponse signup(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : "DEALER");
        user.setOperator(request.getOperator());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getRole(), user.getOperator());
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getRole(), user.getOperator());
    }
}