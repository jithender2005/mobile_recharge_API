package telecom.recharge.mobile_recharge_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// here where the details come after the login is generated

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private String operator;
}