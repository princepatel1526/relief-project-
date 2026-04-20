package com.disasterrelief.config;

import com.disasterrelief.entity.DisasterType;
import com.disasterrelief.entity.Role;
import com.disasterrelief.repository.DisasterTypeRepository;
import com.disasterrelief.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final DisasterTypeRepository disasterTypeRepository;

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedDisasterTypes();
    }

    private void seedRoles() {
        Map<Role.RoleName, String> standardRoles = Map.of(
            Role.RoleName.ROLE_CITIZEN,         "Citizen reporting incidents and requesting support",
            Role.RoleName.ROLE_VOLUNTEER,       "Field volunteer providing relief",
            Role.RoleName.ROLE_RESPONDER,       "Professional first responder",
            Role.RoleName.ROLE_NGO_COORDINATOR, "NGO coordinator managing partner operations",
            Role.RoleName.ROLE_ADMIN,           "System administrator with operational access",
            Role.RoleName.ROLE_SUPER_ADMIN,     "Super administrator with full cross-region access"
        );

        Set<String> legacyRoles = Set.of("ROLE_COORDINATOR", "ROLE_DONOR", "ROLE_NGO");

        try {
            roleRepository.findAll().forEach(role -> {
                if (legacyRoles.contains(role.getName().name())) {
                    roleRepository.delete(role);
                    log.info("Removed legacy role: {}", role.getName());
                }
            });
        } catch (Exception ex) {
            log.warn("Skipping legacy role cleanup due to role mapping mismatch: {}", ex.getMessage());
        }

        standardRoles.forEach((name, desc) -> {
            try {
                Role role = roleRepository.findByName(name).orElseGet(() -> {
                    Role r = new Role();
                    r.setName(name);
                    return r;
                });
                role.setDescription(desc);
                roleRepository.save(role);
            } catch (DataAccessException ex) {
                // Never crash startup because of a seed mismatch; log and continue.
                log.warn("Skipping role seed for {} due to DB constraint mismatch: {}", name, ex.getMostSpecificCause().getMessage());
            }
        });
    }

    private void seedDisasterTypes() {
        List<String[]> types = List.of(
            new String[]{"Flood",           "Flooding due to heavy rainfall or river overflow",  "flood"},
            new String[]{"Earthquake",      "Seismic activity causing structural damage",        "earthquake"},
            new String[]{"Cyclone",         "Tropical cyclone with strong winds and rainfall",   "cyclone"},
            new String[]{"Drought",         "Extended period of water scarcity",                 "drought"},
            new String[]{"Landslide",       "Sudden movement of rock and debris down a slope",  "landslide"},
            new String[]{"Industrial Fire", "Fire at industrial or commercial sites",            "fire"},
            new String[]{"Tsunami",         "Large ocean waves caused by seismic activity",      "tsunami"},
            new String[]{"Chemical Spill",  "Hazardous chemical leak requiring evacuation",     "chemical"}
        );

        types.forEach(t -> {
            if (disasterTypeRepository.findByName(t[0]).isEmpty()) {
                DisasterType dt = new DisasterType();
                dt.setName(t[0]);
                dt.setDescription(t[1]);
                dt.setIcon(t[2]);
                disasterTypeRepository.save(dt);
                log.info("Seeded disaster type: {}", t[0]);
            }
        });
    }
}
