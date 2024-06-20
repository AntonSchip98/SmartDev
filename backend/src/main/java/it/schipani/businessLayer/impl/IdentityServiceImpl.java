package it.schipani.businessLayer.impl;

import it.schipani.businessLayer.dto.IdentityDto.CreateIdentityDto;
import it.schipani.businessLayer.dto.IdentityDto.IdentityDto;
import it.schipani.businessLayer.dto.IdentityDto.UpdateIdentityDto;
import it.schipani.businessLayer.exceptions.PersistEntityException;
import it.schipani.businessLayer.services.IdentityService;
import it.schipani.dataLayer.entities.Identity;
import it.schipani.dataLayer.entities.Task;
import it.schipani.dataLayer.repositories.IdentityRepository;
import it.schipani.dataLayer.repositories.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentityServiceImpl implements IdentityService {

    private final IdentityRepository identityRepository;

    private final UserRepository userRepository;

    @Override
    public IdentityDto createIdentity(Long userId, CreateIdentityDto identityDto) {
        var user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User not found with id: {}", userId);
            return new EntityNotFoundException("User not found");
        });

        if (user.getIdentities().size() >= 3) {
            log.error("User with id {} cannot have more than 3 identities", userId);
            throw new EntityExistsException("User cannot have more than 3 identities");
        }

        try {
            Identity identity = new Identity();
            BeanUtils.copyProperties(identityDto, identity);
            identity.setUser(user);

            user.getIdentities().add(identity);
            identityRepository.save(identity);

            log.info("Identity created successfully for userId: {}", userId);
            return IdentityDto.builder()
                    .withId(identity.getId())
                    .withTitle(identity.getTitle())
                    .withDescription(identity.getDescription())
                    .withCreatedAt(identity.getCreatedAt())
                    .withUserId(identity.getUser().getId())
                    .withTasks(identity.getTasks().stream()
                            .map(Task::getId).collect(Collectors.toList()))
                    .build();
        } catch (Exception e) {
            log.error("Exception saving identity {}", identityDto, e);
            throw new PersistEntityException(identityDto);
        }
    }

    @Override
    public Optional<IdentityDto> getIdentityById(Long id) {
        return identityRepository.findById(id).map(identity -> {
            log.info("Identity retrieved successfully with id: {}", id);
            return IdentityDto.builder()
                    .withId(identity.getId())
                    .withTitle(identity.getTitle())
                    .withDescription(identity.getDescription())
                    .withCreatedAt(identity.getCreatedAt())
                    .withUserId(identity.getUser().getId())
                    .withTasks(identity.getTasks().stream().map(Task::getId)
                            .collect(Collectors.toList()))
                    .build();
        });
    }

    @Override
    public Optional<IdentityDto> updateIdentity(Long id, UpdateIdentityDto identityDto) {
        return identityRepository.findById(id).map(identity -> {
            BeanUtils.copyProperties(identityDto, identity);
            identityRepository.save(identity);
            log.info("Identity updated successfully with id: {}", id);
            return IdentityDto.builder()
                    .withId(identity.getId())
                    .withTitle(identity.getTitle())
                    .withDescription(identity.getDescription())
                    .withCreatedAt(identity.getCreatedAt())
                    .withUserId(identity.getUser().getId())
                    .withTasks(identity.getTasks().stream().map(Task::getId).collect(Collectors.toList()))
                    .build();
        });
    }

    @Override
    public void deleteIdentity(Long id) {
        if (!identityRepository.existsById(id)) {
            log.error("Identity not found with id: {}", id);
            throw new EntityNotFoundException("Identity not found");
        }
        identityRepository.deleteById(id);
        log.info("Identity deleted successfully with id: {}", id);
    }

    @Override
    public List<IdentityDto> getAllIdentitiesByUser(Long userId) {
        return identityRepository.findAllByUserId(userId).stream()
                .map(identity -> {
                    log.info("Identity retrieved successfully for userId: {}", userId);
                    return IdentityDto.builder()
                            .withId(identity.getId())
                            .withTitle(identity.getTitle())
                            .withDescription(identity.getDescription())
                            .withCreatedAt(identity.getCreatedAt())
                            .withUserId(identity.getUser().getId())
                            .withTasks(identity.getTasks().stream().map(Task::getId)
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());
    }
}
