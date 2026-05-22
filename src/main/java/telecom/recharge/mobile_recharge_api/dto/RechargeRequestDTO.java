package telecom.recharge.mobile_recharge_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import telecom.recharge.mobile_recharge_api.enums.OperatorType;

import java.math.BigDecimal;

@Data
public class RechargeRequestDTO {

    @NotBlank(message = "msisdn is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "msisdn must be exactly 10 digits")
    private String msisdn;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be a positive number")
    private BigDecimal amount;

    @NotNull(message = "operator is required")
    private OperatorType operator;
}