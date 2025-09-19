package edu.escuelaing.ecicare.premios.models;

import edu.escuelaing.ecicare.premios.models.dto.RedeemableDto;
import edu.escuelaing.ecicare.premios.models.entity.Award;
import edu.escuelaing.ecicare.premios.models.entity.Redeemable;
import edu.escuelaing.ecicare.premios.models.entity.RedeemableId;
import edu.escuelaing.ecicare.retos.models.entity.Challenge;
import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
import edu.escuelaing.ecicare.utils.models.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Redeemable Model Tests")
class RedeemableModelTest {

    private Redeemable redeemable;
    private RedeemableId redeemableId;
    private Challenge testChallenge;
    private Award testAward;
    private UserEcicare testUser;

    @BeforeEach
    void setUp() {
        LocalDateTime testDateTime = LocalDateTime.now();
        
        testUser = UserEcicare.builder()
                .idEci(1L)
                .name("Test User")
                .email("test@escuelaing.edu.co")
                .password("password123")
                .role(Role.ADMINISTRATION)
                .registrationDate(testDateTime)
                .hasMedicalApprove(true)
                .build();

        testChallenge = Challenge.builder()
                .name("Test Challenge")
                .description("Test Challenge Description")
                .imageUrl("/images/test-challenge.png")
                .phrase("Test Phrase")
                .registered(new ArrayList<>())
                .build();

        testAward = Award.builder()
                .awardId(1L)
                .name("Test Award")
                .description("Test Award Description")
                .inStock(10)
                .imageUrl("/images/test-award.png")
                .creationDate(testDateTime)
                .updateDate(testDateTime)
                .createdBy(testUser)
                .updatedBy(testUser)
                .build();

        redeemableId = RedeemableId.builder()
                .challengeName("Test Challenge")
                .awardId(1L)
                .build();

        redeemable = Redeemable.builder()
                .id(redeemableId)
                .challenge(testChallenge)
                .award(testAward)
                .limitDays(30)
                .build();
    }

    @Test
    @DisplayName("Should create Redeemable with all fields")
    void shouldCreateRedeemableWithAllFields() {
        assertNotNull(redeemable);
        assertNotNull(redeemable.getId());
        assertEquals("Test Challenge", redeemable.getId().getChallengeName());
        assertEquals(1L, redeemable.getId().getAwardId());
        assertEquals(testChallenge, redeemable.getChallenge());
        assertEquals(testAward, redeemable.getAward());
        assertEquals(30, redeemable.getLimitDays());
    }

    @Test
    @DisplayName("Should create Redeemable with required fields only")
    void shouldCreateRedeemableWithRequiredFieldsOnly() {
        RedeemableId minimalId = RedeemableId.builder()
                .challengeName("Minimal Challenge")
                .awardId(2L)
                .build();

        Redeemable minimalRedeemable = Redeemable.builder()
                .id(minimalId)
                .build();

        assertNotNull(minimalRedeemable);
        assertNotNull(minimalRedeemable.getId());
        assertEquals("Minimal Challenge", minimalRedeemable.getId().getChallengeName());
        assertEquals(2L, minimalRedeemable.getId().getAwardId());
        assertNull(minimalRedeemable.getChallenge());
        assertNull(minimalRedeemable.getAward());
        assertNull(minimalRedeemable.getLimitDays());
    }

    @Test
    @DisplayName("Should handle null limit days")
    void shouldHandleNullLimitDays() {
        redeemable.setLimitDays(null);
        assertNull(redeemable.getLimitDays());
    }

    @Test
    @DisplayName("Should handle zero limit days")
    void shouldHandleZeroLimitDays() {
        redeemable.setLimitDays(0);
        assertEquals(0, redeemable.getLimitDays());
    }

    @Test
    @DisplayName("Should handle negative limit days")
    void shouldHandleNegativeLimitDays() {
        redeemable.setLimitDays(-1);
        assertEquals(-1, redeemable.getLimitDays());
    }

    @Test
    @DisplayName("Should handle large limit days")
    void shouldHandleLargeLimitDays() {
        redeemable.setLimitDays(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, redeemable.getLimitDays());
    }

    @Test
    @DisplayName("Should update redeemable fields correctly")
    void shouldUpdateRedeemableFieldsCorrectly() {
        Challenge newChallenge = Challenge.builder()
                .name("Updated Challenge")
                .description("Updated Description")
                .build();

        Award newAward = Award.builder()
                .awardId(2L)
                .name("Updated Award")
                .description("Updated Description")
                .build();

        RedeemableId newId = RedeemableId.builder()
                .challengeName("Updated Challenge")
                .awardId(2L)
                .build();

        redeemable.setId(newId);
        redeemable.setChallenge(newChallenge);
        redeemable.setAward(newAward);
        redeemable.setLimitDays(60);

        assertEquals(newId, redeemable.getId());
        assertEquals("Updated Challenge", redeemable.getId().getChallengeName());
        assertEquals(2L, redeemable.getId().getAwardId());
        assertEquals(newChallenge, redeemable.getChallenge());
        assertEquals(newAward, redeemable.getAward());
        assertEquals(60, redeemable.getLimitDays());
    }

    @Test
    @DisplayName("Should create Redeemable using no-args constructor")
    void shouldCreateRedeemableUsingNoArgsConstructor() {
        Redeemable emptyRedeemable = new Redeemable();
        
        assertNotNull(emptyRedeemable);
        assertNull(emptyRedeemable.getId());
        assertNull(emptyRedeemable.getChallenge());
        assertNull(emptyRedeemable.getAward());
        assertNull(emptyRedeemable.getLimitDays());
    }

    @Test
    @DisplayName("Should create Redeemable using all-args constructor")
    void shouldCreateRedeemableUsingAllArgsConstructor() {
        Redeemable fullRedeemable = new Redeemable(
                redeemableId,
                testChallenge,
                testAward,
                45
        );

        assertNotNull(fullRedeemable);
        assertEquals(redeemableId, fullRedeemable.getId());
        assertEquals(testChallenge, fullRedeemable.getChallenge());
        assertEquals(testAward, fullRedeemable.getAward());
        assertEquals(45, fullRedeemable.getLimitDays());
    }

    @Test
    @DisplayName("Should test RedeemableId creation and properties")
    void shouldTestRedeemableIdCreationAndProperties() {
        RedeemableId id = RedeemableId.builder()
                .challengeName("ID Test Challenge")
                .awardId(100L)
                .build();

        assertNotNull(id);
        assertEquals("ID Test Challenge", id.getChallengeName());
        assertEquals(100L, id.getAwardId());
    }

    @Test
    @DisplayName("Should test RedeemableId equals and hashcode")
    void shouldTestRedeemableIdEqualsAndHashcode() {
        RedeemableId id1 = RedeemableId.builder()
                .challengeName("Same Challenge")
                .awardId(50L)
                .build();

        RedeemableId id2 = RedeemableId.builder()
                .challengeName("Same Challenge")
                .awardId(50L)
                .build();

        RedeemableId id3 = RedeemableId.builder()
                .challengeName("Different Challenge")
                .awardId(75L)
                .build();

        assertEquals(id1, id2);
        assertNotEquals(id3, id1);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("Should create RedeemableId using no-args constructor")
    void shouldCreateRedeemableIdUsingNoArgsConstructor() {
        RedeemableId emptyId = new RedeemableId();
        
        assertNotNull(emptyId);
        assertNull(emptyId.getChallengeName());
        assertNull(emptyId.getAwardId());
    }

    @Test
    @DisplayName("Should create RedeemableId using all-args constructor")
    void shouldCreateRedeemableIdUsingAllArgsConstructor() {
        RedeemableId fullId = new RedeemableId("Constructor Challenge", 200L);

        assertNotNull(fullId);
        assertEquals("Constructor Challenge", fullId.getChallengeName());
        assertEquals(200L, fullId.getAwardId());
    }

    @Test
    @DisplayName("Should test RedeemableDto creation and properties")
    void shouldTestRedeemableDtoCreationAndProperties() {
        RedeemableDto dto = RedeemableDto.builder()
                .awardId(300L)
                .limitDays(90)
                .build();

        assertNotNull(dto);
        assertEquals(300L, dto.getAwardId());
        assertEquals(90, dto.getLimitDays());
    }

    @Test
    @DisplayName("Should test RedeemableDto equals and hashcode")
    void shouldTestRedeemableDtoEqualsAndHashcode() {
        RedeemableDto dto1 = RedeemableDto.builder()
                .awardId(100L)
                .limitDays(30)
                .build();

        RedeemableDto dto2 = RedeemableDto.builder()
                .awardId(100L)
                .limitDays(30)
                .build();

        RedeemableDto dto3 = RedeemableDto.builder()
                .awardId(200L)
                .limitDays(60)
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto3, dto1);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Should test RedeemableDto toString")
    void shouldTestRedeemableDtoToString() {
        RedeemableDto dto = RedeemableDto.builder()
                .awardId(500L)
                .limitDays(120)
                .build();

        String toStringResult = dto.toString();
        
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("500"));
        assertTrue(toStringResult.contains("120"));
    }

    @Test
    @DisplayName("Should create RedeemableDto with no-args constructor")
    void shouldCreateRedeemableDtoWithNoArgsConstructor() {
        RedeemableDto emptyDto = new RedeemableDto();
        
        assertNotNull(emptyDto);
        assertNull(emptyDto.getAwardId());
        assertNull(emptyDto.getLimitDays());
    }

    @Test
    @DisplayName("Should create RedeemableDto with all-args constructor")
    void shouldCreateRedeemableDtoWithAllArgsConstructor() {
        RedeemableDto fullDto = new RedeemableDto("New Challenge",600L, 150);

        assertNotNull(fullDto);
        assertEquals(600L, fullDto.getAwardId());
        assertEquals(150, fullDto.getLimitDays());
    }

    @Test
    @DisplayName("Should handle relationship between Redeemable and Challenge")
    void shouldHandleRelationshipBetweenRedeemableAndChallenge() {
        assertEquals(testChallenge.getName(), redeemable.getId().getChallengeName());
        assertEquals(testChallenge, redeemable.getChallenge());
        
        // Verificar que el challenge puede cambiar
        Challenge newChallenge = Challenge.builder()
                .name("New Challenge")
                .description("New Description")
                .build();
        
        redeemable.setChallenge(newChallenge);
        assertEquals(newChallenge, redeemable.getChallenge());
        // Nota: En un caso real, también debería actualizar el ID, 
        // pero aquí solo estamos probando la relación
    }

    @Test
    @DisplayName("Should handle relationship between Redeemable and Award")
    void shouldHandleRelationshipBetweenRedeemableAndAward() {
        assertEquals(testAward.getAwardId(), redeemable.getId().getAwardId());
        assertEquals(testAward, redeemable.getAward());
        
        // Verificar que el award puede cambiar
        Award newAward = Award.builder()
                .awardId(999L)
                .name("New Award")
                .description("New Description")
                .build();
        
        redeemable.setAward(newAward);
        assertEquals(newAward, redeemable.getAward());
        // Nota: En un caso real, también debería actualizar el ID,
        // pero aquí solo estamos probando la relación
    }

    @Test
    @DisplayName("Should validate RedeemableId as Serializable")
    void shouldValidateRedeemableIdAsSerializable() {
        // RedeemableId debe implementar Serializable para ser usada como clave compuesta
        assertTrue(redeemableId instanceof java.io.Serializable);
    }

    @Test
    @DisplayName("Should handle edge cases for limit days")
    void shouldHandleEdgeCasesForLimitDays() {
        // Casos extremos comunes para límite de días
        int[] testValues = {1, 7, 30, 90, 365, 1000};
        
        for (int value : testValues) {
            redeemable.setLimitDays(value);
            assertEquals(value, redeemable.getLimitDays());
        }
    }
}
