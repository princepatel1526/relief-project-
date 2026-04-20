package com.disasterrelief.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class DashboardStatsResponse {
    private long activeDisasters;
    private long criticalDisasters;
    private long totalVolunteers;
    private long availableVolunteers;
    private long pendingRequests;
    private long fulfilledRequests;
    private long totalDonations;
    private BigDecimal totalDonationAmount;
    private long lowStockItems;
}
