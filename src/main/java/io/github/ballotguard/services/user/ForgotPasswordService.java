package io.github.ballotguard.services.user;

import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.repositories.UserRepository;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class ForgotPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CreateResponseUtil createResponseUtil;



    @Transactional
    public ResponseEntity resetPassword(String oldPassword, String newPassword) throws Exception {
        try{
            Optional<UserEntity> authenticatedUserEntity = getAuthenticatedUserUtil.getAuthenticatedUser();

            if(!authenticatedUserEntity.isPresent()){
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(createResponseUtil.createResponseBody(false, "User does not exist"));

            }else if(!passwordEncoder.matches(oldPassword, authenticatedUserEntity.get().getPassword())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponseUtil.createResponseBody(false, "Wrong previous password"));

            }else if(newPassword.length() < 8 || newPassword.length() > 50){

                return ResponseEntity.badRequest()
                        .body(createResponseUtil.createResponseBody(false, "Password must be between 8 and 50 characters"));

            }else if(newPassword.equals(oldPassword)){
                return ResponseEntity.badRequest()
                        .body(createResponseUtil.createResponseBody(false, "New password cannot be same as your previous password"));
            }else{
                authenticatedUserEntity.get().setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(authenticatedUserEntity.get());
                return ResponseEntity.ok().body(createResponseUtil.createResponseBody(true, "Password changed"));

            }

        }catch (Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }


}
