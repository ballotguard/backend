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
            UserEntity authenticatedUser = getAuthenticatedUserUtil.getAuthenticatedUser();

            if(!passwordEncoder.matches(oldPassword, authenticatedUser.getPassword())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponseUtil.createResponseBody(false, "Previous password did not match"));

            }else if(newPassword.length() < 8 || newPassword.length() > 50){

                return ResponseEntity.badRequest()
                        .body(createResponseUtil.createResponseBody(false, "Password must be between 8 and 50 characters"));

            }else if(newPassword.equals(oldPassword)){
                return ResponseEntity.badRequest()
                        .body(createResponseUtil.createResponseBody(false, "New password cannot be same as your previous password"));
            }else{
                authenticatedUser.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(authenticatedUser);
                return ResponseEntity.ok().body(createResponseUtil.createResponseBody(true, "Password successfully changed"));

            }

        }catch (Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }


}
