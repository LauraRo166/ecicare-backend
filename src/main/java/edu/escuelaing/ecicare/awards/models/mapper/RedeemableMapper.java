package edu.escuelaing.ecicare.awards.models.mapper;

import edu.escuelaing.ecicare.awards.models.dto.RedeemableResponse;
import edu.escuelaing.ecicare.awards.models.entity.Redeemable;

public class RedeemableMapper {

    private RedeemableMapper() {
    }

    public static RedeemableResponse toResponse(Redeemable redeemable) {
        return RedeemableResponse.builder()
                .challengeName(redeemable.getId().getChallengeName())
                .awardId(redeemable.getId().getAwardId())
                .limitDays(redeemable.getLimitDays())
                .build();
    }
}
