package it.schipani.presentationLayer.controller;

import it.schipani.businessLayer.dto.IdentityDto.CreateIdentityDto;
import it.schipani.businessLayer.dto.IdentityDto.IdentityDto;
import it.schipani.businessLayer.dto.IdentityDto.UpdateIdentityDto;
import it.schipani.businessLayer.services.IdentityService;
import it.schipani.dataLayer.repositories.UserRepository;
import it.schipani.presentationLayer.model.Requests.CreateIdentityRequest;
import it.schipani.presentationLayer.model.Requests.UpdateIdentityRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/identities")
@RequiredArgsConstructor
public class IdentityController {

    private final IdentityService identityService;
    private final UserRepository userRepository;

    @PostMapping("/{userId}")
    public ResponseEntity<IdentityDto> createIdentity(@PathVariable Long userId, @RequestBody CreateIdentityRequest identityRequest) {
        var createIdentityDto = CreateIdentityDto.builder()
                .withTitle(identityRequest.title())
                .withDescription(identityRequest.description())
                .build();

        var createdIdentity = identityService.createIdentity(userId, createIdentityDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdIdentity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdentityDto> getIdentity(@PathVariable Long id) {
        Optional<IdentityDto> identity = identityService.getIdentityById(id);

        return identity.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<IdentityDto> updateIdentity(@PathVariable Long id, @RequestBody UpdateIdentityRequest identityRequest) {
        var updateIdentityDto = UpdateIdentityDto.builder()
                .withTitle(identityRequest.title())
                .withDescription(identityRequest.description())
                .build();

        Optional<IdentityDto> updatedIdentity = identityService.updateIdentity(id, updateIdentityDto);

        return updatedIdentity.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIdentity(@PathVariable Long id) {
        identityService.deleteIdentity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<IdentityDto>> getAllIdentitiesByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = userRepository.findOneByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found")).getId();
        List<IdentityDto> identities = identityService.getAllIdentitiesByUser(userId);
        return ResponseEntity.ok(identities);
    }
}
