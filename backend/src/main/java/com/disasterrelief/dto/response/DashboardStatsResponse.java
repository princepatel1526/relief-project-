package com.disasterrelief.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data @Builder
public class DashboardStatsResponse {
    // Core stats
    private long activeDisasters;
    private long criticalDisasters;
    private long totalVolunteers;
    private long availableVolunteers;
    private long pendingRequests;
    private long fulfilledRequests;
    private long totalDonations;
    private BigDecimal totalDonationAmount;
    private long lowStockItems;

    // New analytics fields
    private long totalDisasters;
    private long totalRequests;
    private long resolvedDisasters;
    private long verifiedRequests;
    private long rejectedRequests;
    private Double avgPriorityScore;

    // Chart data
    private Map<String, Long> requestsByType;       // type → count
    private Map<String, Long> requestsByStatus;     // status → count
    private Map<String, Long> disastersByType;      // type name → count
    private Map<String, Long> disastersBySeverity;  // severity → count
    private List<Map<String, Object>> recentActivity; // last 30 days daily counts
}
