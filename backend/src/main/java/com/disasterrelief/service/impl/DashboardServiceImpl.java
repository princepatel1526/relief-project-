package com.disasterrelief.service.impl;

import com.disasterrelief.dto.response.DashboardStatsResponse;
import com.disasterrelief.entity.Disaster;
import com.disasterrelief.entity.Donation;
import com.disasterrelief.entity.ReliefRequest;
import com.disasterrelief.entity.Volunteer;
import com.disasterrelief.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl {

    private final DisasterRepository disasterRepository;
    private final VolunteerRepository volunteerRepository;
    private final ReliefRequestRepository reliefRequestRepository;
    private final DonationRepository donationRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        return DashboardStatsResponse.builder()
                .activeDisasters(disasterRepository.countByStatus(Disaster.DisasterStatus.ACTIVE))
                .criticalDisasters(disasterRepository.countBySeverity(Disaster.Severity.CRITICAL))
                .totalVolunteers(volunteerRepository.count())
                .availableVolunteers(volunteerRepository.findByAvailability(
                        Volunteer.Availability.AVAILABLE,
                        org.springframework.data.domain.Pageable.unpaged()).getTotalElements())
                .pendingRequests(reliefRequestRepository.countByStatus(ReliefRequest.RequestStatus.PENDING))
                .fulfilledRequests(reliefRequestRepository.countByStatus(ReliefRequest.RequestStatus.FULFILLED))
                .totalDonations(donationRepository.countByStatus(Donation.DonationStatus.CONFIRMED))
                .totalDonationAmount(donationRepository.sumConfirmedMonetaryDonations())
                .lowStockItems((long) inventoryRepository.findLowStockItems().size())
                .build();
    }
}
