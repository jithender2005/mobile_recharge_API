package telecom.recharge.mobile_recharge_api.dto;

import lombok.Data;
//                      after auth request holds user name and password
@Data
public class AuthRequest {
    private String username;
    private String password;
    private String role;        // "ADMIN" or "DEALER"
    private String operator;    // "JIO", "AIRTEL", "BSNL", "VI" — null for ADMIN
}