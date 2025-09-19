package edu.escuelaing.ecicare.premios.models;

import edu.escuelaing.ecicare.premios.models.dto.AwardDto;
import edu.escuelaing.ecicare.premios.models.entity.Award;
import edu.escuelaing.ecicare.premios.models.entity.Redeemable;
import edu.escuelaing.ecicare.usuarios.models.entity.UserEcicare;
import edu.escuelaing.ecicare.utils.models.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Award Model Tests")
class AwardModelTest {

    private Award award;
    private UserEcicare testUser;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();
        
        testUser = UserEcicare.builder()
                .idEci(1L)
                .name("Test User")
                .email("test@escuelaing.edu.co")
                .password("password123")
                .role(Role.ADMINISTRATION)
                .registrationDate(testDateTime)
                .hasMedicalApprove(true)
                .build();

        award = Award.builder()
                .awardId(1L)
                .name("Test Award")
                .description("Test Award Description")
                .inStock(10)
                .imageUrl("/images/test-award.png")
                .creationDate(testDateTime)
                .updateDate(testDateTime)
                .createdBy(testUser)
                .updatedBy(testUser)
                .redeemables(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Should create Award with all fields")
    void shouldCreateAwardWithAllFields() {
        assertNotNull(award);
        assertEquals(1L, award.getAwardId());
        assertEquals("Test Award", award.getName());
        assertEquals("Test Award Description", award.getDescription());
        assertEquals(10, award.getInStock());
        assertEquals("/images/test-award.png", award.getImageUrl());
        assertEquals(testDateTime, award.getCreationDate());
        assertEquals(testDateTime, award.getUpdateDate());
        assertEquals(testUser, award.getCreatedBy());
        assertEquals(testUser, award.getUpdatedBy());
        assertNotNull(award.getRedeemables());
        assertTrue(award.getRedeemables().isEmpty());
    }

    @Test
    @DisplayName("Should create Award with required fields only")
    void shouldCreateAwardWithRequiredFieldsOnly() {
        Award minimalAward = Award.builder()
                .name("Minimal Award")
                .build();

        assertNotNull(minimalAward);
        assertEquals("Minimal Award", minimalAward.getName());
        assertNull(minimalAward.getAwardId());
        assertNull(minimalAward.getDescription());
        assertNull(minimalAward.getInStock());
        assertNull(minimalAward.getImageUrl());
        assertNull(minimalAward.getCreationDate());
        assertNull(minimalAward.getUpdateDate());
        assertNull(minimalAward.getCreatedBy());
        assertNull(minimalAward.getUpdatedBy());
        assertNull(minimalAward.getRedeemables());
    }

    @Test
    @DisplayName("Should handle null description")
    void shouldHandleNullDescription() {
        award.setDescription(null);
        assertNull(award.getDescription());
    }

    @Test
    @DisplayName("Should handle empty description")
    void shouldHandleEmptyDescription() {
        award.setDescription("");
        assertEquals("", award.getDescription());
    }

    @Test
    @DisplayName("Should handle long description within limits")
    void shouldHandleLongDescription() {
        String longDescription = "A".repeat(500); // 500 caracteres (límite según la entidad)
        award.setDescription(longDescription);
        assertEquals(longDescription, award.getDescription());
        assertEquals(500, award.getDescription().length());
    }

    @Test
    @DisplayName("Should handle zero stock")
    void shouldHandleZeroStock() {
        award.setInStock(0);
        assertEquals(0, award.getInStock());
    }

    @Test
    @DisplayName("Should handle negative stock")
    void shouldHandleNegativeStock() {
        award.setInStock(-5);
        assertEquals(-5, award.getInStock());
    }

    @Test
    @DisplayName("Should handle large stock numbers")
    void shouldHandleLargeStock() {
        award.setInStock(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, award.getInStock());
    }

    @Test
    @DisplayName("Should update award fields correctly")
    void shouldUpdateAwardFieldsCorrectly() {
        LocalDateTime newDateTime = LocalDateTime.now().plusDays(1);
        UserEcicare newUser = UserEcicare.builder()
                .idEci(2L)
                .name("New User")
                .email("newuser@escuelaing.edu.co")
                .password("newpassword")
                .role(Role.STUDENT)
                .registrationDate(newDateTime)
                .hasMedicalApprove(false)
                .build();

        award.setName("Updated Award");
        award.setDescription("Updated Description");
        award.setInStock(20);
        award.setImageUrl("/images/updated-award.png");
        award.setUpdateDate(newDateTime);
        award.setUpdatedBy(newUser);

        assertEquals("Updated Award", award.getName());
        assertEquals("Updated Description", award.getDescription());
        assertEquals(20, award.getInStock());
        assertEquals("/images/updated-award.png", award.getImageUrl());
        assertEquals(newDateTime, award.getUpdateDate());
        assertEquals(newUser, award.getUpdatedBy());
        // Los campos de creación no deben cambiar
        assertEquals(testDateTime, award.getCreationDate());
        assertEquals(testUser, award.getCreatedBy());
    }

    @Test
    @DisplayName("Should handle redeemables relationship")
    void shouldHandleRedeemablesRelationship() {
        assertTrue(award.getRedeemables().isEmpty());

        // Simular adición de redeemables
        Set<Redeemable> redeemables = new HashSet<>();
        award.setRedeemables(redeemables);

        assertEquals(redeemables, award.getRedeemables());
        assertTrue(award.getRedeemables().isEmpty());
    }

    @Test
    @DisplayName("Should test Award equals and hashcode")
    void shouldTestAwardEqualsAndHashcode() {
        Award award1 = Award.builder()
                .awardId(1L)
                .name("Same Award")
                .description("Same Description")
                .build();

        Award award2 = Award.builder()
                .awardId(1L)
                .name("Same Award")
                .description("Same Description")
                .build();

        Award award3 = Award.builder()
                .awardId(2L)
                .name("Different Award")
                .description("Different Description")
                .build();

        // Test equals
        assertEquals(award1, award2);
        assertNotEquals(award3, award1);
        assertNotEquals(null, award1);
        assertNotEquals("not an award", award1);

        // Test hashcode
        assertEquals(award1.hashCode(), award2.hashCode());
    }

    @Test
    @DisplayName("Should test Award toString")
    void shouldTestAwardToString() {
        String toStringResult = award.toString();
        
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("Test Award"));
        assertTrue(toStringResult.contains("Test Award Description"));
        assertTrue(toStringResult.contains("10"));
    }

    @Test
    @DisplayName("Should create Award using no-args constructor")
    void shouldCreateAwardUsingNoArgsConstructor() {
        Award emptyAward = new Award();
        
        assertNotNull(emptyAward);
        assertNull(emptyAward.getAwardId());
        assertNull(emptyAward.getName());
        assertNull(emptyAward.getDescription());
        assertNull(emptyAward.getInStock());
    }

    @Test
    @DisplayName("Should create Award using all-args constructor")
    void shouldCreateAwardUsingAllArgsConstructor() {
        Set<Redeemable> redeemables = new HashSet<>();
        
        Award fullAward = new Award(
                1L, 
                "Constructor Award", 
                "Constructor Description", 
                5, 
                "/images/constructor.png", 
                testDateTime, 
                testDateTime, 
                testUser, 
                testUser, 
                redeemables
        );

        assertNotNull(fullAward);
        assertEquals(1L, fullAward.getAwardId());
        assertEquals("Constructor Award", fullAward.getName());
        assertEquals("Constructor Description", fullAward.getDescription());
        assertEquals(5, fullAward.getInStock());
        assertEquals("/images/constructor.png", fullAward.getImageUrl());
        assertEquals(testDateTime, fullAward.getCreationDate());
        assertEquals(testDateTime, fullAward.getUpdateDate());
        assertEquals(testUser, fullAward.getCreatedBy());
        assertEquals(testUser, fullAward.getUpdatedBy());
        assertEquals(redeemables, fullAward.getRedeemables());
    }

    @Test
    @DisplayName("Should test AwardDto creation and properties")
    void shouldTestAwardDtoCreationAndProperties() {
        AwardDto awardDto = AwardDto.builder()
                .name("DTO Award")
                .description("DTO Description")
                .inStock(15)
                .imageUrl("/images/dto-award.png")
                .updatedBy(testUser)
                .build();

        assertNotNull(awardDto);
        assertEquals("DTO Award", awardDto.getName());
        assertEquals("DTO Description", awardDto.getDescription());
        assertEquals(15, awardDto.getInStock());
        assertEquals("/images/dto-award.png", awardDto.getImageUrl());
        assertEquals(testUser, awardDto.getUpdatedBy());
    }

    @Test
    @DisplayName("Should test AwardDto equals and hashcode")
    void shouldTestAwardDtoEqualsAndHashcode() {
        AwardDto dto1 = AwardDto.builder()
                .name("Same DTO")
                .description("Same Description")
                .inStock(10)
                .build();

        AwardDto dto2 = AwardDto.builder()
                .name("Same DTO")
                .description("Same Description")
                .inStock(10)
                .build();

        AwardDto dto3 = AwardDto.builder()
                .name("Different DTO")
                .description("Different Description")
                .inStock(20)
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto3, dto1);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Should test AwardDto toString")
    void shouldTestAwardDtoToString() {
        AwardDto awardDto = AwardDto.builder()
                .name("ToString DTO")
                .description("ToString Description")
                .inStock(5)
                .build();

        String toStringResult = awardDto.toString();
        
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("ToString DTO"));
        assertTrue(toStringResult.contains("ToString Description"));
        assertTrue(toStringResult.contains("5"));
    }

    @Test
    @DisplayName("Should create AwardDto with no-args constructor")
    void shouldCreateAwardDtoWithNoArgsConstructor() {
        AwardDto emptyDto = new AwardDto();
        
        assertNotNull(emptyDto);
        assertNull(emptyDto.getName());
        assertNull(emptyDto.getDescription());
        assertNull(emptyDto.getInStock());
        assertNull(emptyDto.getImageUrl());
        assertNull(emptyDto.getUpdatedBy());
    }

    @Test
    @DisplayName("Should create AwardDto with all-args constructor")
    void shouldCreateAwardDtoWithAllArgsConstructor() {
        AwardDto fullDto = new AwardDto(
                "Constructor DTO",
                "Constructor Description",
                25,
                "/images/constructor-dto.png",
                testUser
        );

        assertNotNull(fullDto);
        assertEquals("Constructor DTO", fullDto.getName());
        assertEquals("Constructor Description", fullDto.getDescription());
        assertEquals(25, fullDto.getInStock());
        assertEquals("/images/constructor-dto.png", fullDto.getImageUrl());
        assertEquals(testUser, fullDto.getUpdatedBy());
    }
}
