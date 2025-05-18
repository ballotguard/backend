package io.github.ballotguard.services;

import io.github.ballotguard.entities.UserEntity;
import io.github.ballotguard.repositories.UserRepository;
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



    @Transactional
    public ResponseEntity resetPassword(String oldPassword, String newPassword) throws Exception {
        try{
            Optional<UserEntity> authenticatedUserEntity = getAuthenticatedUserUtil.getAuthenticatedUser();
            if (authenticatedUserEntity.isPresent() && passwordEncoder.matches(oldPassword, authenticatedUserEntity.get().getPassword())) {
                authenticatedUserEntity.get().setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(authenticatedUserEntity.get());
                return ResponseEntity.ok().build();
            }else if(!authenticatedUserEntity.isPresent()){
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
            }else if(!passwordEncoder.matches(oldPassword, authenticatedUserEntity.get().getPassword())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }catch (Exception e){
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }


}
