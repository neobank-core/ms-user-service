package org.neobank.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.neobank.userservice.enums.KycStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_kyc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserKyc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    private KycStatus status;

    private String documentType;

    private LocalDateTime submittedAt;



    @PrePersist
    public void prePersist() {
        this.submittedAt = LocalDateTime.now();
    }
}
