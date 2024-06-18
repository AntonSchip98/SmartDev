package it.schipani.businessLayer.impl;

import it.schipani.businessLayer.dto.IdentityRequest;
import it.schipani.businessLayer.dto.IdentityResponse;
import it.schipani.businessLayer.services.IdentityService;
import it.schipani.dataLayer.entities.Identity;
import it.schipani.dataLayer.repositories.IdentityRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentityServiceImpl implements IdentityService {

    private final IdentityRepository identityRepository;

    @Override
    @Transactional
    public IdentityResponse create(IdentityRequest identityRequest) {
        try {
            Identity identity = new Identity();
            BeanUtils.copyProperties(identityRequest, identity);
            identity.setCreatedAt(LocalDateTime.now());
            identityRepository.save(identity);
            IdentityResponse responseDTO = new IdentityResponse();
            BeanUtils.copyProperties(identity, responseDTO);
            return responseDTO;
        } catch (Exception e) {
            log.error("Exception creating identity", e);
            throw new PersistenceException("Failed to create identity");
        }
    }

    @Override
    public Optional<IdentityResponse> get(long id) {
        try {
            Identity identity = identityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Identity not found"));
            IdentityResponse responseDTO = new IdentityResponse();
            BeanUtils.copyProperties(identity, responseDTO);
            return Optional.of(responseDTO);
        } catch (EntityNotFoundException e) {
            log.error("Identity not found for id {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<IdentityResponse> getAll() {
        return identityRepository.findAll().stream().map(identity -> {
            IdentityResponse responseDTO = new IdentityResponse();
            BeanUtils.copyProperties(identity, responseDTO);
            return responseDTO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<IdentityResponse> update(long id, IdentityRequest identityRequest) {
        try {
            Identity identity = identityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Identity not found"));
            BeanUtils.copyProperties(identityRequest, identity, "id", "createdAt");
            identityRepository.save(identity);
            IdentityResponse responseDTO = new IdentityResponse();
            BeanUtils.copyProperties(identity, responseDTO);
            return Optional.of(responseDTO);
        } catch (EntityNotFoundException e) {
            log.error("Identity not found for id {}", id, e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Exception updating identity", e);
            throw new PersistenceException("Failed to update identity");
        }
    }

    @Override
    @Transactional
    public void delete(long id) {
        try {
            Identity identity = identityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Identity not found"));
            identityRepository.delete(identity);
        } catch (EntityNotFoundException e) {
            log.error("Identity not found for id {}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Exception deleting identity with id {}", id, e);
            throw new PersistenceException("Failed to delete identity");
        }
    }
}
