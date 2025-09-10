package io.github.ballotguard.services.user;

import io.github.ballotguard.entities.user.UserEntity;
import io.github.ballotguard.entities.user.UserSettingsEntity;
import io.github.ballotguard.repositories.UserSettingsRepository;
import io.github.ballotguard.utilities.CreateResponseUtil;
import io.github.ballotguard.utilities.GetAuthenticatedUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserSettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;


    public ResponseEntity getUserSettingsEntity(String userSettingsId) throws Exception {
      try{
          if(userSettingsId == null){
              return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
          }else if(userSettingsId.isEmpty()){
              return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
          }

          Optional<UserSettingsEntity> userSettingsEntity = userSettingsRepository.findById(userSettingsId);
          if(userSettingsEntity.isEmpty()){
              UserEntity userEntity = getAuthenticatedUserUtil.getAuthenticatedUser();
              UserSettingsEntity newUserSettingsEntity = new UserSettingsEntity(UUID.randomUUID().toString(), userEntity.getEmail(), "English", "Light", true, true);
              userSettingsRepository.save(newUserSettingsEntity);
              userEntity.setUserSettingsEntityId(newUserSettingsEntity.getId());
          }
          return ResponseEntity
                  .ok()
                  .body(createResponseUtil
                          .createResponseBody(true, "User settings found", "userSettings", userSettingsEntity));
      }catch(Exception e){
          throw new Exception(e);
      }
    }

    @Transactional
    public ResponseEntity updateUserSettingsEntity(String userSettingsId, UserSettingsEntity newUserSettingsEntity) throws Exception {
        try{
            if(userSettingsId == null || newUserSettingsEntity == null){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }else if(userSettingsId.isEmpty()){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Optional<UserSettingsEntity> oldUserSettingsEntity = userSettingsRepository.findById(userSettingsId);


            newUserSettingsEntity.setId(userSettingsId);
            newUserSettingsEntity.setUserEmail(oldUserSettingsEntity.get().getUserEmail());
            userSettingsRepository.save(newUserSettingsEntity);

            return ResponseEntity
                    .ok()
                    .body(createResponseUtil
                            .createResponseBody(true, "User settings updated", "userSettings", newUserSettingsEntity));
        }catch(Exception e){
            throw new Exception(e);
        }
    }
}
