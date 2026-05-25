package telecom.recharge.mobile_recharge_api.repository;

import telecom.recharge.mobile_recharge_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

    //              UserRepository  finds your user from DB--->user entity

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}