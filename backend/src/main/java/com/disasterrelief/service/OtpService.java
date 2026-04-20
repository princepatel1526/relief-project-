package com.disasterrelief.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory OTP store. Each phone gets one active OTP at a time.
 * Expires after 5 minutes. In production replace generate() body
 * with an actual SMS provider call (Twilio, MSG91, etc.) and remove
 * the returned raw OTP from the API response.
 */
@Slf4j
@Service
public class OtpService {

    private static final int    EXPIRY_MINUTES = 5;
    private static final SecureRandom RNG = new SecureRandom();

    private final Map<String, OtpEntry> store = new ConcurrentHashMap<>();

    /** Generate, store and return a 6-digit OTP for the given phone. */
    public String generate(String phone) {
        String otp = String.format("%06d", RNG.nextInt(1_000_000));
        store.put(phone, new OtpEntry(otp,
                LocalDateTime.now().plusMinutes(EXPIRY_MINUTES)));
        log.info("[OTP] Generated for phone={} — OTP={} (valid {} min)", phone, otp, EXPIRY_MINUTES);
        return otp;
    }

    /**
     * Verify an OTP. Returns true and removes the entry on success.
     * Returns false on mismatch or expiry.
     */
    public boolean verify(String phone, String otp) {
        OtpEntry entry = store.get(phone);
        if (entry == null) {
            log.warn("[OTP] No pending OTP for phone={}", phone);
            return false;
        }
        if (LocalDateTime.now().isAfter(entry.expiry())) {
            store.remove(phone);
            log.warn("[OTP] OTP expired for phone={}", phone);
            return false;
        }
        if (entry.otp().equals(otp)) {
            store.remove(phone);
            log.info("[OTP] Verified successfully for phone={}", phone);
            return true;
        }
        log.warn("[OTP] Wrong OTP attempt for phone={}", phone);
        return false;
    }

    private record OtpEntry(String otp, LocalDateTime expiry) {}
}
