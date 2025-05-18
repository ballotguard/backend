package io.github.ballotguard.utilities;

import io.github.ballotguard.entities.UserEntity;
import io.github.ballotguard.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Slf4j
@Component
public class GetAuthenticatedUserUtil {

    @Autowired
    private UserRepository userRepository;

    public Optional<UserEntity> getAuthenticatedUser() {
       try{
           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
           String email = authentication.getName();
           return userRepository.findByEmail(email);
       }catch(Exception e){
           log.error(e.getMessage());
           return Optional.empty();
       }
    }
}