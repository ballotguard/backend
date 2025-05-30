package io.github.ballotguard.services.user;

import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.entities.user.UserVerificationEntity;
import io.github.ballotguard.repositories.UserRepository;
import io.github.ballotguard.repositories.UserVerificationRepository;
import io.github.ballotguard.services.EmailService;
import io.github.ballotguard.utilities.CreateResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
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

    @Autowired
    CreateResponseUtil createResponseUtil;

    public UserVerificationEntity createUserVerificationEntity(String userId){
        UserVerificationEntity userVerificationEntity =
                new UserVerificationEntity(UUID.randomUUID().toString().replace("-", ""), userId, "", Instant.now());

        return userVerificationRepository.save(userVerificationEntity);
    }

    public ResponseEntity sendVerificationCodeEmail(UserEntity userEntity, String emailSubject, String emailBody, String responseMessage) throws Exception {
        try{

            UserVerificationEntity userVerificationEntity ;
            Optional<UserVerificationEntity> optionalUserVerificationEntity = userVerificationRepository.findById(userEntity.getUserVerificationEntityId());
            if(!optionalUserVerificationEntity.isPresent()){
                UserVerificationEntity newUserVerificationEntity = createUserVerificationEntity(userEntity.getUserId());
                userEntity.setUserVerificationEntityId(newUserVerificationEntity.getId());
                userRepository.save(userEntity);
                userVerificationEntity = newUserVerificationEntity;

            }else{
                userVerificationEntity = optionalUserVerificationEntity.get();
            }

            String verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
            userVerificationEntity.setVerificationCode(verificationCode);
            userVerificationEntity.setVerificationCodeExpirationTime(Instant.now().plus(Duration.ofMinutes(5)));
            userVerificationRepository.save(userVerificationEntity);
            emailService.sendEmail(userEntity.getEmail(), emailSubject, verificationCode, emailBody);
            log.debug("Email sent");
            return ResponseEntity.ok().body(createResponseUtil.createResponseBody(true, responseMessage));

        }catch (Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public ResponseEntity verifyVerificationCode(UserEntity userEntity, String verificationCode, Boolean isForgotPasswordVerification, String responseMessage) throws Exception {
        try{
            Optional<UserVerificationEntity> userVerificationEntity = userVerificationRepository.findById(userEntity.getUserVerificationEntityId());

            if(userVerificationEntity.isPresent()){
                if(userVerificationEntity.get().getVerificationCode().equals("")){
                    return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                            .body(createResponseUtil.createResponseBody(false, "This user has not requested for verification code"));

                }else if(!userVerificationEntity.get().getVerificationCode().equals(verificationCode)){

                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "This code is not valid"));

                }else if(userVerificationEntity.get().getVerificationCode().equals(verificationCode)
                        && userVerificationEntity.get().getVerificationCodeExpirationTime().isBefore(Instant.now())){

                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(createResponseUtil.createResponseBody(false, "This code has expired"));

                }else{

                    userVerificationEntity.get().setVerificationCode("");
                    userVerificationRepository.save(userVerificationEntity.get());

                    if (!isForgotPasswordVerification) {
                        userEntity.setVerified(true);
                        userRepository.save(userEntity);
                        return ResponseEntity.ok().body(createResponseUtil.createResponseBody(true, responseMessage));
                    }
                    return  ResponseEntity.ok().build();

                }
            }else{
                throw  new Exception();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
