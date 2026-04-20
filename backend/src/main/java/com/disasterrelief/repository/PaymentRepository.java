package com.disasterrelief.repository;

import com.disasterrelief.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentOrderId(String paymentOrderId);
    Optional<Payment> findByPaymentId(String paymentId);
    boolean existsByPaymentId(String paymentId);
}
