package telecom.recharge.mobile_recharge_api.dto;

import lombok.Data;
import telecom.recharge.mobile_recharge_api.enums.OperatorType;
import telecom.recharge.mobile_recharge_api.enums.RechargeStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RechargeResponseDTO {

    private Long id;
    private String msisdn;
    private BigDecimal amount;
    private OperatorType operator;
    private RechargeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String submittedByUsername; // Added this field to capture the dealer's username
}