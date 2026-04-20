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

import java.util.LinkedHashMap;
import java.util.Map;

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
        // Build request type distribution
        Map<String, Long> reqByType = new LinkedHashMap<>();
        reliefRequestRepository.countGroupByType()
                .forEach(row -> reqByType.put(row[0].toString(), (Long) row[1]));

        // Build request status distribution
        Map<String, Long> reqByStatus = new LinkedHashMap<>();
        reliefRequestRepository.countGroupByStatus()
                .forEach(row -> reqByStatus.put(row[0].toString(), (Long) row[1]));

        // Build disaster type distribution
        Map<String, Long> disByType = new LinkedHashMap<>();
        disasterRepository.countGroupByType()
                .forEach(row -> disByType.put(row[0].toString(), (Long) row[1]));

        // Build disaster severity distribution
        Map<String, Long> disBySeverity = new LinkedHashMap<>();
        disasterRepository.countGroupBySeverity()
                .forEach(row -> disBySeverity.put(row[0].toString(), (Long) row[1]));

        return DashboardStatsResponse.builder()
                // Core stats
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
                // Analytics
                .totalDisasters(disasterRepository.count())
                .totalRequests(reliefRequestRepository.count())
                .resolvedDisasters(disasterRepository.countByStatus(Disaster.DisasterStatus.RESOLVED))
                .verifiedRequests(reliefRequestRepository.countByStatus(ReliefRequest.RequestStatus.VERIFIED))
                .rejectedRequests(reliefRequestRepository.countByStatus(ReliefRequest.RequestStatus.REJECTED))
                .avgPriorityScore(reliefRequestRepository.avgPriorityScore())
                // Chart data
                .requestsByType(reqByType)
                .requestsByStatus(reqByStatus)
                .disastersByType(disByType)
                .disastersBySeverity(disBySeverity)
                .build();
    }
}
