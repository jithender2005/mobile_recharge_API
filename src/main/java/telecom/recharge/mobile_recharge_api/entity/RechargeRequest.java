package telecom.recharge.mobile_recharge_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import telecom.recharge.mobile_recharge_api.enums.OperatorType;
import telecom.recharge.mobile_recharge_api.enums.RechargeStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(
        name = "recharge_request",
        indexes = {
                @Index(name = "idx_msisdn", columnList = "msisdn"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_user_id", columnList = "user_id")
        })
public class RechargeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    @Pattern(regexp = "^[0-9]{10}$", message = "msisdn must be exactly 10 digits")
    private String msisdn;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OperatorType operator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RechargeStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true) // Nullable so old data doesn't crash
    private User submittedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.status = RechargeStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}