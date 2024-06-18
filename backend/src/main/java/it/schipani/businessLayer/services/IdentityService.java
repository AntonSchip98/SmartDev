package it.schipani.businessLayer.services;

import it.schipani.businessLayer.dto.IdentityRequest;
import it.schipani.businessLayer.dto.IdentityResponse;

import java.util.List;
import java.util.Optional;

public interface IdentityService {
    IdentityResponse create(IdentityRequest identityRequestDTO);
    Optional<IdentityResponse> get(long id);
    List<IdentityResponse> getAll();
    Optional<IdentityResponse> update(long id, IdentityRequest identityRequestDTO);
    void delete(long id);
}
