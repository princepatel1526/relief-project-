package com.disasterrelief.controller;

import com.disasterrelief.dto.request.NewsUpdateRequest;
import com.disasterrelief.dto.response.NewsUpdateResponse;
import com.disasterrelief.entity.Disaster;
import com.disasterrelief.entity.NewsUpdate;
import com.disasterrelief.service.impl.NewsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsServiceImpl newsService;

    @GetMapping
    public ResponseEntity<Page<NewsUpdateResponse>> list(
            @RequestParam(required = false) NewsUpdate.NewsStatus status,
            @RequestParam(required = false) Disaster.Severity severity,
            @RequestParam(required = false) String disasterType,
            @RequestParam(required = false) String region,
            @RequestParam(required = false, name = "viewSort") String viewSort,
            //@RequestParam(required = false) String sort,
            @RequestParam(required = false, name = "q") String query,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(newsService.list(status, severity, disasterType, region, viewSort, query, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<NewsUpdateResponse>> search(
            @RequestParam(name = "q") String query,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(newsService.list(null, null, null, null, "LATEST", query, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsUpdateResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'NGO_COORDINATOR')")
    public ResponseEntity<NewsUpdateResponse> create(@Valid @RequestBody NewsUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'NGO_COORDINATOR')")
    public ResponseEntity<NewsUpdateResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody NewsUpdateRequest request) {
        return ResponseEntity.ok(newsService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'NGO_COORDINATOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
