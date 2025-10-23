package edu.escuelaing.ecicare.challenges.models.dto;

import edu.escuelaing.ecicare.users.models.dto.UserEcicareDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleChallengesUsersDTO {
    private String name;
    private String description;
    private String imageUrl;
    private List<ChallengeUsersDTO> challenges;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeUsersDTO {
        private String name;
        private String description;
        private String imageUrl;
        private List<UserEcicareDto> registeredUsers;
    }
}
