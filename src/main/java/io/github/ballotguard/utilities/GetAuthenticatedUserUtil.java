package io.github.ballotguard.utilities;

import io.github.ballotguard.entities.user.UserEntity;
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

    public UserEntity getAuthenticatedUser() {
       try{
           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
           String email = authentication.getName();
           Optional<UserEntity> user = userRepository.findByEmail(email);
           if(user.isPresent()) {
               return user.get();
           }else{
               return null;
           }
       }catch(Exception e){
           log.error(e.getMessage());
           return null;
       }
    }
}