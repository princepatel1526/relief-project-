package com.disasterrelief.repository;

import com.disasterrelief.entity.PaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentEventRepository extends JpaRepository<PaymentEvent, Long> {
    Optional<PaymentEvent> findByPaymentIdAndEventType(Long paymentId, String eventType);
    boolean existsByPaymentIdAndEventType(Long paymentId, String eventType);
}
