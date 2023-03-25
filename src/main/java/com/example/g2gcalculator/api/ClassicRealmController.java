package com.example.g2gcalculator.api;

import com.example.g2gcalculator.dto.RealmResponse;
import com.example.g2gcalculator.service.RealmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wow-classic/v1/realms")
@RequiredArgsConstructor
public class ClassicRealmController {

    private final RealmService classicRealmService;

    @GetMapping("/{realmName}")
    public ResponseEntity<?> getRealm(@PathVariable String realmName) {
        return ResponseEntity.ok(classicRealmService.getRealm(realmName));
    }

    @GetMapping
    public ResponseEntity<List<RealmResponse>> getAllRealms(Pageable pageable) {
        return ResponseEntity.ok(classicRealmService.getAllRealms(pageable));
    }
}