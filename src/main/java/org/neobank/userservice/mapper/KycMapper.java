package org.neobank.userservice.mapper;

import org.neobank.userservice.dto.KycResponse;
import org.neobank.userservice.entity.UserKyc;
import org.springframework.stereotype.Component;

@Component
public class KycMapper {

    public KycResponse toResponse(UserKyc userKyc) {
        return new KycResponse(
                userKyc.getId(),
                userKyc.getStatus(),
                userKyc.getDocumentType(),
                userKyc.getSubmittedAt()
        );
    }
}
