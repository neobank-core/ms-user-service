package org.neobank.userservice.service;

import lombok.RequiredArgsConstructor;
import org.neobank.userservice.entity.User;
import org.neobank.userservice.entity.UserKyc;
import org.neobank.userservice.enums.KycStatus;
import org.neobank.userservice.exception.KycAlreadyExistsException;
import org.neobank.userservice.repository.UserKycRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KycService {

    private final UserKycRepository userKycRepository;

    public UserKyc submitKyc(User user, String documentType) {
        boolean kycExists = userKycRepository.findByUser(user).isPresent();
        if (kycExists) {
            throw new KycAlreadyExistsException("KYC information already exists for user: " + user.getId());
        }
        UserKyc userKyc = UserKyc.builder()
                .user(user)
                .documentType(documentType)
                .status(KycStatus.PENDING)
                .build();
        return userKycRepository.save(userKyc);
    }
}
