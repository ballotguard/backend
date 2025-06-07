package io.github.ballotguard.utilities;

import io.github.ballotguard.entities.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPreconditionsCheckUtil {

    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    public ResponseEntity checkUserVerification(UserEntity user) {

        if(!user.isVerified()){
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .body(createResponseUtil.createResponseBody(false, "User is not verified"));
        }


        return ResponseEntity.ok().build();

    }
}
