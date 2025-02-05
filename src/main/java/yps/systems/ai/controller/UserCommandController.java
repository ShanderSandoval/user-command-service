package yps.systems.ai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import yps.systems.ai.model.User;
import yps.systems.ai.repository.IUserRepository;

import java.util.Optional;

@RestController
@RequestMapping("/command/userService")
public class UserCommandController {

    private final IUserRepository userRepository;
    private final KafkaTemplate<String, User> kafkaTemplate;

    @Value("${env.kafka.topicEvent}")
    private String kafkaTopicEvent;

    @Autowired
    public UserCommandController(IUserRepository userRepository, KafkaTemplate<String, User> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/{personElementId}")
    ResponseEntity<String> createUser(@RequestBody User user, @PathVariable String personElementId) {
        User userSaved = userRepository.save(user);
        userRepository.setUserRelationTo(personElementId, user.getElementId());
        Message<User> message = MessageBuilder
                .withPayload(userSaved)
                .setHeader(KafkaHeaders.TOPIC, kafkaTopicEvent)
                .setHeader("eventType", "CREATE_USER")
                .setHeader("source", "userService")
                .build();
        kafkaTemplate.send(message);
        return new ResponseEntity<>("Person saved with ID: " + userSaved.getElementId(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{elementId}")
    public ResponseEntity<String> deleteUser(@PathVariable String elementId) {
        Optional<User> userOptional = userRepository.findById(elementId);
        if (userOptional.isPresent()) {
            userRepository.deleteUserRelation(elementId);
            userRepository.deleteById(elementId);
            Message<String> message = MessageBuilder
                    .withPayload(elementId)
                    .setHeader(KafkaHeaders.TOPIC, kafkaTopicEvent)
                    .setHeader("eventType", "DELETE_USER")
                    .setHeader("source", "userService")
                    .build();
            kafkaTemplate.send(message);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not founded", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{elementId}")
    public ResponseEntity<String> updateUSer(@PathVariable String elementId, @RequestBody User user) {
        Optional<User> userOptional = userRepository.findById(elementId);
        if (userOptional.isPresent()) {
            user.setElementId(userOptional.get().getElementId());
            userRepository.save(user);
            Message<User> message = MessageBuilder
                    .withPayload(user)
                    .setHeader(KafkaHeaders.TOPIC, kafkaTopicEvent)
                    .setHeader("eventType", "UPDATE_USER")
                    .setHeader("source", "userService")
                    .build();
            kafkaTemplate.send(message);
            return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not founded", HttpStatus.NOT_FOUND);
        }
    }

}
