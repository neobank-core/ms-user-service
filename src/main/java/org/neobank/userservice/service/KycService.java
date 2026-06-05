package org.neobank.userservice.service;

import lombok.RequiredArgsConstructor;
import org.neobank.userservice.entity.User;
import org.neobank.userservice.entity.UserKyc;
import org.neobank.userservice.enums.KycStatus;
import org.neobank.userservice.exception.KycAlreadyExistsException;
import org.neobank.userservice.exception.KycNotFoundException;
import org.neobank.userservice.repository.UserKycRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KycService {

    private final UserKycRepository userKycRepository;

    public UserKyc submitKyc(User user, String documentType, String documentNumber) {
        boolean kycExists = userKycRepository.findByUser(user).isPresent();
        if (kycExists) {
            throw new KycAlreadyExistsException("KYC information already exists for user: " + user.getId());
        }
        UserKyc userKyc = UserKyc.builder()
                .user(user)
                .documentType(documentType)
                .documentNumber(documentNumber)
                .status(KycStatus.PENDING)
                .build();
        return userKycRepository.save(userKyc);
    }

    public UserKyc approveKyc(Long kycId) {
        UserKyc kyc = userKycRepository.findById(kycId)
                .orElseThrow(() -> new KycNotFoundException("KYC not found for ID: " + kycId));
        kyc.setStatus(KycStatus.APPROVED);
        kyc.setReviewedAt(java.time.LocalDateTime.now());
        return userKycRepository.save(kyc);
    }

    public UserKyc rejectKyc(Long kycId) {
        UserKyc kyc = userKycRepository.findById(kycId)
                .orElseThrow(() -> new KycNotFoundException("KYC not found for ID: " + kycId));
        kyc.setStatus(KycStatus.REJECTED);
        kyc.setReviewedAt(java.time.LocalDateTime.now());
        return userKycRepository.save(kyc);
    }
}
