package edu.escuelaing.ecicare.challenges.services;

import edu.escuelaing.ecicare.challenges.models.dto.ChallengeResponse;
import edu.escuelaing.ecicare.challenges.models.dto.ModuleDTO;
import edu.escuelaing.ecicare.challenges.models.dto.ModuleResponse;
import edu.escuelaing.ecicare.challenges.models.entity.Challenge;
import edu.escuelaing.ecicare.challenges.models.entity.Module;
import edu.escuelaing.ecicare.challenges.repositories.ChallengeRepository;
import edu.escuelaing.ecicare.challenges.repositories.ModuleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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

        List<ModuleResponse> result = moduleService.getAllModules();

        assertThat(result)
                .isNotNull()
                .hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Module1");
        assertThat(result.get(1).name()).isEqualTo("Module2");
        verify(moduleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no modules exist")
    void getAllModules_whenNoModulesExist_shouldReturnEmptyList() {
        when(moduleRepository.findAll()).thenReturn(Collections.emptyList());
        List<ModuleResponse> result = moduleService.getAllModules();
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

        List<ChallengeResponse> result = moduleService.getChallengesByModule("Module1");
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Challenge1");
    }

    @Test
    @DisplayName("Should update description of existing module")
    void updateModuleDescription_whenModuleExists_shouldUpdateAndSave() {
        ModuleDTO moduleDto = createTestModuleDto("Module1", "New Description", "imageUrl");
        Module module = createTestModule("Module1", "Old Description", Collections.emptyList(), "imageUrl");
        when(moduleRepository.findById("Module1")).thenReturn(Optional.of(module));

        when(moduleRepository.save(any(Module.class))).thenReturn(module);
        ModuleResponse updated = moduleService.updateModuleByName(moduleDto);

        verify(moduleRepository, times(1)).save(any(Module.class));
        assertThat(updated.description()).isEqualTo("New Description");
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
    @DisplayName("Should return total count of modules")
    void getTotalModules_whenCalled_shouldReturnCount() {
        when(moduleRepository.count()).thenReturn(5L);

        int result = moduleService.getTotalModules();

        assertThat(result).isEqualTo(5);
        verify(moduleRepository, times(1)).count();
    }

    @Test
    @DisplayName("Should return paginated modules when valid page and size are provided")
    void getAllModulesPaginated_whenCalledWithValidPageAndSize_shouldReturnPage() {
        Module module1 = createTestModule("Module1", "Description1", Collections.emptyList(), "imageUrl");
        Module module2 = createTestModule("Module2", "Description2", Collections.emptyList(), "imageUrl");

        Page<Module> page = new PageImpl<>(List.of(module1, module2));
        when(moduleRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ModuleResponse> result = moduleService.getAllModulesPaginated(0, 2);

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.getContent().get(0).name()).isEqualTo("Module1");
        assertThat(result.getContent().get(1).name()).isEqualTo("Module2");
        verify(moduleRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should default page to 0 when negative value is provided")
    void getAllModulesPaginated_whenNegativePage_shouldUseZero() {
        Module module = createTestModule("Module1", "Description1", Collections.emptyList(), "imageUrl");
        Page<Module> mockPage = new PageImpl<>(List.of(module));
        when(moduleRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        moduleService.getAllModulesPaginated(-1, 5);
        verify(moduleRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should default size to 10 when zero or negative size is provided")
    void getAllModulesPaginated_whenSizeIsZeroOrNegative_shouldUseDefaultSize10() {
        Module module = createTestModule("Module1", "Description1", Collections.emptyList(), "imageUrl");
        Page<Module> mockPage = new PageImpl<>(List.of(module));
        when(moduleRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        moduleService.getAllModulesPaginated(0, 0);
        verify(moduleRepository, times(1)).findAll(any(Pageable.class));

        moduleService.getAllModulesPaginated(0, -5);
        verify(moduleRepository, times(2)).findAll(any(Pageable.class));
    }

}