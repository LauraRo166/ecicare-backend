package edu.escuelaing.ecicare.awards.models.mapper;

import edu.escuelaing.ecicare.awards.models.dto.AwardResponse;
import edu.escuelaing.ecicare.awards.models.dto.RedeemableResponse;
import edu.escuelaing.ecicare.awards.models.entity.Award;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class AwardMapper {

    private AwardMapper() {
    }

    public static AwardResponse toResponse(Award award) {
        List<RedeemableResponse> redeemableResponses = null;

        if (award.getRedeemables() != null) {
            redeemableResponses = award.getRedeemables().stream()
                    .map(RedeemableMapper::toResponse)
                    .toList();
        }

        return AwardResponse.builder()
                .id(award.getAwardId())
                .name(award.getName())
                .description(award.getDescription())
                .inStock(award.getInStock())
                .imageUrl(award.getImageUrl())
                .redeemables(redeemableResponses)
                .build();
    }

}