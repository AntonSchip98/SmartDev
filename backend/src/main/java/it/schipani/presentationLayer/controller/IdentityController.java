package it.schipani.presentationLayer.controller;

import it.schipani.businessLayer.dto.IdentityRequest;
import it.schipani.businessLayer.dto.IdentityResponse;
import it.schipani.businessLayer.impl.IdentityServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/identities")
@RequiredArgsConstructor
public class IdentityController {

    private final IdentityServiceImpl identityService;

    @PostMapping
    public ResponseEntity<IdentityResponse> createIdentity(@Validated @RequestBody IdentityRequest identityRequest) {
        try {
            IdentityResponse response = identityService.create(identityRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (PersistenceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdentityResponse> getIdentity(@PathVariable("id") long id) {
        Optional<IdentityResponse> response = identityService.get(id);
        return response.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<IdentityResponse>> getAllIdentities() {
        List<IdentityResponse> identities = identityService.getAll();
        return ResponseEntity.ok(identities);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IdentityResponse> updateIdentity(
            @PathVariable("id") long id,
            @Validated @RequestBody IdentityRequest identityRequest) {
        Optional<IdentityResponse> response = identityService.update(id, identityRequest);
        return response.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIdentity(@PathVariable("id") long id) {
        try {
            identityService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (PersistenceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
