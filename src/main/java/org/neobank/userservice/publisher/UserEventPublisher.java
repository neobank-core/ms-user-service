package org.neobank.userservice.publisher;

import lombok.RequiredArgsConstructor;
import org.neobank.userservice.event.UserRegisteredEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserRegistered(UserRegisteredEvent event) {
        kafkaTemplate.send(
                "user.registered",
                event.userId().toString(),
                event
        );
    }
}
