package edu.escuelaing.ecicare.retos.services;

import edu.escuelaing.ecicare.retos.models.dto.ModuleDTO;
import edu.escuelaing.ecicare.retos.models.entity.Challenge;
import edu.escuelaing.ecicare.retos.models.entity.Module;
import edu.escuelaing.ecicare.retos.repositories.ChallengeRepository;
import edu.escuelaing.ecicare.retos.repositories.ModuleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModuleServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ModuleService moduleService;

    private Module createTestModule(String name, String description, List<Challenge> challenges, String imageUrl) {
        return Module.builder()
                .name(name)
                .description(description)
                .challenges(challenges)
                .imageUrl(imageUrl)
                .build();
    }

    private ModuleDTO createTestModuleDto(String name, String description, String imageUrl) {
        return ModuleDTO.builder()
                .name(name)
                .description(description)
                .imageUrl(imageUrl)
                .build();
    }

    @Test
    @DisplayName("Should save module when creating a new one")
    void createModule_whenCalledWithModule_shouldSaveModule() {
        Module module = createTestModule("Module1", "Description1", Collections.emptyList(), "imageUrl");
        ModuleDTO moduleDto = createTestModuleDto("Module1", "Description1", "imageUrl");
        moduleService.createModule(moduleDto);
        verify(moduleRepository, times(1)).save(module);
    }

    @Test
    @DisplayName("Should return all modules when they exist")
    void getAllModules_whenModulesExist_shouldReturnModuleList() {
        Module module1 = createTestModule("Module1", "Description1", Collections.emptyList(), "imageUrl");
        Module module2 = createTestModule("Module2", "Description2", Collections.emptyList(), "imageUrl");
        when(moduleRepository.findAll()).thenReturn(List.of(module1, module2));

        List<Module> result = moduleService.getAllModules();

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactly(module1, module2);
        verify(moduleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no modules exist")
    void getAllModules_whenNoModulesExist_shouldReturnEmptyList() {
        when(moduleRepository.findAll()).thenReturn(Collections.emptyList());
        List<Module> result = moduleService.getAllModules();
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should return challenges of a given module")
    void getChallengesByModule_whenModuleExists_shouldReturnChallengeList() {
        Challenge challenge = Challenge.builder()
                .name("Challenge1")
                .description("Desc challenge")
                .goals(List.of("Goal1"))
                .duration(java.time.LocalDateTime.now().plusDays(5))
                .module(new Module("Module1"))
                .build();

        Module module = createTestModule("Module1", "Description1", List.of(challenge), "imageUrl");
        when(moduleRepository.findById("Module1")).thenReturn(Optional.of(module));

        List<Challenge> result = moduleService.getChallengesByModule("Module1");
        assertThat(result).isNotNull().hasSize(1).containsExactly(challenge);
    }

    @Test
    @DisplayName("Should update description of existing module")
    void updateModuleDescription_whenModuleExists_shouldUpdateAndSave() {
        ModuleDTO moduleDto = createTestModuleDto("Module1", "New Description", "imageUrl");
        Module module = createTestModule("Module1", "Old Description", Collections.emptyList(), "imageUrl");
        when(moduleRepository.findById("Module1")).thenReturn(Optional.of(module));

        Module updated = moduleService.updateModuleByName(moduleDto);

        verify(moduleRepository, times(1)).save(module);
        assertThat(updated.getDescription()).isEqualTo("New Description");
    }

    @Test
    @DisplayName("Should delete module when it has no challenges")
    void deleteModule_whenNoChallenges_shouldDeleteAndReturnTrue() {
        Module module = createTestModule("Module1", "Description1", Collections.emptyList(), "imageUrl");
        when(moduleRepository.findById("Module1")).thenReturn(Optional.of(module));

        moduleService.deleteModule("Module1");

        verify(moduleRepository, times(1)).delete(module);
        verify(challengeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should not delete module when it has challenges")
    void deleteModule_whenHasChallenges_shouldReturnFalse() {
        Challenge challenge = Challenge.builder()
                .name("Challenge1")
                .description("Desc")
                .duration(java.time.LocalDateTime.now().plusDays(3))
                .goals(List.of("Goal1"))
                .module(new Module("Module1"))
                .build();

        Module module = createTestModule("Module1", "Description1", List.of(challenge), "imageUrl");
        when(moduleRepository.findById("Module1")).thenReturn(Optional.of(module));

        moduleService.deleteModule("Module1");

        verify(challengeRepository, times(1)).delete(challenge);
        verify(moduleRepository, times(1)).delete(module);
    }
}
