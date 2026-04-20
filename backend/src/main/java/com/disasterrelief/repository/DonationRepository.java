package com.disasterrelief.repository;

import com.disasterrelief.entity.Donation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    Page<Donation> findByDisasterId(Long disasterId, Pageable pageable);

    Page<Donation> findByStatus(Donation.DonationStatus status, Pageable pageable);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.status = 'CONFIRMED' AND d.donationType = 'MONETARY'")
    BigDecimal sumConfirmedMonetaryDonations();

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.disaster.id = :disasterId AND d.status = 'CONFIRMED'")
    BigDecimal sumConfirmedDonationsByDisaster(@Param("disasterId") Long disasterId);

    long countByStatus(Donation.DonationStatus status);
}
