package com.disasterrelief.controller;

import com.disasterrelief.entity.DisasterType;
import com.disasterrelief.repository.DisasterTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disaster-types")
@RequiredArgsConstructor
public class DisasterTypeController {

    private final DisasterTypeRepository disasterTypeRepository;

    @GetMapping
    public ResponseEntity<List<DisasterType>> getAllTypes() {
        return ResponseEntity.ok(disasterTypeRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisasterType> getType(@PathVariable Long id) {
        return disasterTypeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
