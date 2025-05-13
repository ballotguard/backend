package io.github.ballotguard.services;

import io.github.ballotguard.entities.UserEntity;
import io.github.ballotguard.entities.UserVerificationEntity;
import io.github.ballotguard.repositories.UserRepository;
import io.github.ballotguard.repositories.UserVerificationRepository;
import io.github.ballotguard.utilities.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserVerificationService {

    @Autowired
    UserVerificationRepository userVerificationRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    UserRepository userRepository;

    public UserVerificationEntity createUserVerificationEntity(String userId){
        UserVerificationEntity userVerificationEntity =
                new UserVerificationEntity(UUID.randomUUID().toString().replace("-", ""), userId, "", Instant.now());

        return userVerificationRepository.save(userVerificationEntity);
    }

    public ResponseEntity sendVerificationCodeEmail(UserEntity userEntity, String emailSubject, String emailBody) throws Exception {
        try{
            Optional<UserVerificationEntity> userVerificationEntity = userVerificationRepository.findById(userEntity.getUserVerificationEntityId());
            if(userVerificationEntity.isPresent()){
                String verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
                userVerificationEntity.get().setVerificationCode(verificationCode);
                userVerificationEntity.get().setVerificationCodeExpirationTime(Instant.now().plus(Duration.ofMinutes(5)));
                userVerificationRepository.save(userVerificationEntity.get());
                emailService.sendEmail(userEntity.getEmail(), emailSubject, verificationCode, emailBody);
                log.debug("Email sent");
                return ResponseEntity.ok().build();

            }else{
                UserVerificationEntity newUserVerificationEntity = createUserVerificationEntity(userEntity.getId());
                userEntity.setUserVerificationEntityId(newUserVerificationEntity.getId());
                userRepository.save(userEntity);
                return new ResponseEntity(HttpStatus.PRECONDITION_FAILED);
            }
        }catch (Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public ResponseEntity verifyVerificationCode(UserEntity userEntity, String verificationCode, Boolean isForgotPasswordVerification) throws Exception {
        try{
            Optional<UserVerificationEntity> userVerificationEntity = userVerificationRepository.findById(userEntity.getUserVerificationEntityId());

            if(userVerificationEntity.isPresent() && !userVerificationEntity.get().getVerificationCode().equals("")
                    && userVerificationEntity.get().getVerificationCode().equals(verificationCode)
                    && userVerificationEntity.get().getVerificationCodeExpirationTime().isAfter(Instant.now())){

                userVerificationEntity.get().setVerificationCode("");
                userVerificationRepository.save(userVerificationEntity.get());

                if (!isForgotPasswordVerification) {
                    userEntity.setVerified(true);
                    userRepository.save(userEntity);
                }
                return ResponseEntity.ok().build();


            }else if(userVerificationEntity.isPresent() && userVerificationEntity.get().getVerificationCode().equals(verificationCode)
                    && userVerificationEntity.get().getVerificationCodeExpirationTime().isBefore(Instant.now())){

                Map<String, Object> response= new HashMap<>();
                response.put("message", "This code has expired, request a new code");
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);

            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
