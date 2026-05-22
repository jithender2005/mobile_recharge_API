package telecom.recharge.mobile_recharge_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import telecom.recharge.mobile_recharge_api.entity.RechargeRequest;
import telecom.recharge.mobile_recharge_api.enums.OperatorType;
import telecom.recharge.mobile_recharge_api.enums.RechargeStatus;

@Repository
public interface RechargeRepository extends JpaRepository<RechargeRequest, Long> {
    Page<RechargeRequest> findByMsisdn(String msisdn, Pageable pageable);
    Page<RechargeRequest> findByStatus(RechargeStatus status, Pageable pageable);

    // Scoped queries for Dealer/Operator role enforcement
    Page<RechargeRequest> findByOperator(OperatorType operator, Pageable pageable);
    Page<RechargeRequest> findByMsisdnAndOperator(String msisdn, OperatorType operator, Pageable pageable);
    Page<RechargeRequest> findByStatusAndOperator(RechargeStatus status, OperatorType operator, Pageable pageable);
}