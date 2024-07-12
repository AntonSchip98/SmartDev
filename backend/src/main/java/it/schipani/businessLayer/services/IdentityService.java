package it.schipani.businessLayer.services;

import it.schipani.businessLayer.dto.IdentityDto.CreateIdentityDto;
import it.schipani.businessLayer.dto.IdentityDto.IdentityDto;
import it.schipani.businessLayer.dto.IdentityDto.UpdateIdentityDto;

import java.util.List;
import java.util.Optional;

public interface IdentityService {
    IdentityDto createIdentity(Long userId, CreateIdentityDto identityDto);
    Optional<IdentityDto> getIdentityById(Long id);
    Optional<IdentityDto> updateIdentity(Long id, UpdateIdentityDto identityDto);
    void deleteIdentity(Long id);
    List<IdentityDto> getAllIdentitiesByUser(Long userId);
}
