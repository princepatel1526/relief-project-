package com.disasterrelief.service;

import com.disasterrelief.entity.ReliefRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Calculates a numeric priority score for a relief request.
 *
 * Score components (max 100):
 *  - Urgency level   : 0–10  → contributes 0–30 pts
 *  - Affected people : 1+   → up to 20 pts
 *  - Elderly/children flag  → +20 pts
 *  - Medical emergency flag → +15 pts
 *  - Request type weight    → up to 10 pts
 *  - Waiting time (hours)   → up to 5 pts
 */
@Service
public class PriorityService {

    public int calculate(ReliefRequest request) {
        int score = 0;

        // 1. Urgency level (1-10 scale → 0-30 points)
        int urgency = request.getUrgencyLevel() != null ? request.getUrgencyLevel() : 5;
        score += (int) ((urgency / 10.0) * 30);

        // 2. Affected people (up to 20 pts)
        int people = request.getAffectedPeople() != null ? request.getAffectedPeople() : 1;
        if      (people >= 500) score += 20;
        else if (people >= 100) score += 15;
        else if (people >= 50)  score += 10;
        else if (people >= 10)  score += 6;
        else                    score += 2;

        // 3. Vulnerable groups (elderly / children)
        if (Boolean.TRUE.equals(request.getHasElderlyChildren())) score += 20;

        // 4. Medical emergency
        if (Boolean.TRUE.equals(request.getIsMedicalEmergency())) score += 15;

        // 5. Request type weight
        score += typeWeight(request.getRequestType());

        // 6. Waiting time bonus (up to 5 pts — 1 pt per 2 hours waiting, max 10 hours)
        if (request.getCreatedAt() != null) {
            long hoursWaiting = ChronoUnit.HOURS.between(request.getCreatedAt(), LocalDateTime.now());
            score += (int) Math.min(5, hoursWaiting / 2);
        }

        return Math.min(score, 100);
    }

    private int typeWeight(ReliefRequest.RequestType type) {
        if (type == null) return 3;
        return switch (type) {
            case RESCUE  -> 10;
            case MEDICAL -> 9;
            case WATER   -> 8;
            case FOOD    -> 7;
            case SHELTER -> 6;
            case CLOTHING -> 3;
            case OTHER   -> 2;
        };
    }
}
