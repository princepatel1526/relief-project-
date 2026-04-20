package com.disasterrelief.service;

import com.disasterrelief.dto.request.DisasterRequest;
import com.disasterrelief.dto.response.DisasterResponse;
import com.disasterrelief.entity.*;
import com.disasterrelief.repository.*;
import com.disasterrelief.service.impl.DisasterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisasterServiceTest {

    @Mock private DisasterRepository disasterRepository;
    @Mock private DisasterTypeRepository disasterTypeRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReliefRequestRepository reliefRequestRepository;
    @Mock private NotificationService notificationService;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks private DisasterServiceImpl disasterService;

    private User mockUser;
    private DisasterType mockType;

    @BeforeEach
    void setup() {
        mockUser = User.builder().id(1L).username("admin").fullName("Admin User").build();
        mockType = DisasterType.builder().id(1L).name("Flood").icon("flood").build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createDisaster_ShouldReturnResponse_WhenValidRequest() {
        DisasterRequest request = new DisasterRequest();
        request.setTitle("Test Flood");
        request.setDisasterTypeId(1L);
        request.setSeverity(Disaster.Severity.HIGH);
        request.setLatitude(19.0760);
        request.setLongitude(72.8777);
        request.setLocationName("Mumbai");
        request.setAffectedPeople(500);

        Disaster savedDisaster = Disaster.builder()
                .id(1L)
                .title("Test Flood")
                .disasterType(mockType)
                .severity(Disaster.Severity.HIGH)
                .status(Disaster.DisasterStatus.REPORTED)
                .reportedBy(mockUser)
                .latitude(19.0760)
                .longitude(72.8777)
                .locationName("Mumbai")
                .affectedPeople(500)
                .build();

        when(disasterTypeRepository.findById(1L)).thenReturn(Optional.of(mockType));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(mockUser));
        when(disasterRepository.save(any(Disaster.class))).thenReturn(savedDisaster);
        when(reliefRequestRepository.countByDisasterIdAndStatus(anyLong(), any())).thenReturn(0L);
        doNothing().when(notificationService).broadcastDisasterAlert(any());

        DisasterResponse response = disasterService.createDisaster(request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Test Flood");
        assertThat(response.getSeverity()).isEqualTo(Disaster.Severity.HIGH);
        assertThat(response.getStatus()).isEqualTo(Disaster.DisasterStatus.REPORTED);
        verify(disasterRepository, times(1)).save(any(Disaster.class));
    }

    @Test
    void updateStatus_ShouldChangeStatus_WhenDisasterExists() {
        Disaster disaster = Disaster.builder()
                .id(1L).title("Flood").disasterType(mockType)
                .severity(Disaster.Severity.HIGH).status(Disaster.DisasterStatus.REPORTED)
                .reportedBy(mockUser).build();

        when(disasterRepository.findById(1L)).thenReturn(Optional.of(disaster));
        when(disasterRepository.save(any())).thenReturn(disaster);
        when(reliefRequestRepository.countByDisasterIdAndStatus(anyLong(), any())).thenReturn(0L);

        DisasterResponse response = disasterService.updateStatus(1L, Disaster.DisasterStatus.ACTIVE);

        assertThat(response).isNotNull();
        verify(disasterRepository).save(argThat(d -> d.getStatus() == Disaster.DisasterStatus.ACTIVE));
    }
}
