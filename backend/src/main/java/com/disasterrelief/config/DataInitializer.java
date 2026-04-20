package com.disasterrelief.config;

import com.disasterrelief.entity.DisasterType;
import com.disasterrelief.entity.Role;
import com.disasterrelief.repository.DisasterTypeRepository;
import com.disasterrelief.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
        Map<Role.RoleName, String> roles = Map.of(
            Role.RoleName.ROLE_ADMIN,         "System administrator with full access",
            Role.RoleName.ROLE_SUPER_ADMIN,   "Super administrator with cross-region access",
            Role.RoleName.ROLE_COORDINATOR,   "Disaster coordinator managing operations",
            Role.RoleName.ROLE_VOLUNTEER,     "Field volunteer providing relief",
            Role.RoleName.ROLE_DONOR,         "Donor contributing resources",
            Role.RoleName.ROLE_CITIZEN,       "Citizen reporting incidents and needs",
            Role.RoleName.ROLE_RESPONDER,     "Professional first responder",
            Role.RoleName.ROLE_NGO,           "NGO coordinator managing partner operations"
        );

        roles.forEach((name, desc) -> {
            if (roleRepository.findByName(name).isEmpty()) {
                Role role = new Role();
                role.setName(name);
                role.setDescription(desc);
                roleRepository.save(role);
                log.info("Seeded role: {}", name);
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
