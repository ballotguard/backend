package io.github.ballotguard.utilities;

import io.github.ballotguard.entities.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ElectionPreconditionsCheckUtil {
    @Autowired
    private GetAuthenticatedUserUtil getAuthenticatedUserUtil;

    @Autowired
    private CreateResponseUtil createResponseUtil;

    @Autowired
    private UserPreconditionsCheckUtil userPreconditionsCheckUtil;

//    public ResponseEntity checkPreconditions(String electionId, String dataToBeCheckedName, String dataToBeChecked) {
//
//
//        ResponseEntity response = checkPreconditions(electionId);
//        if(response.getStatusCode() != HttpStatus.OK) {
//            return response;
//        }
//
//        if(dataToBeChecked != null || dataToBeChecked.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
//                    .body(createResponseUtil.createResponseBody(false, "Election "+dataToBeCheckedName +" is empty"));
//        }
//
//        return ResponseEntity.ok().build();
//
//    }
//
//    public ResponseEntity checkPreconditions(String electionId, UserEntity user) {
//
//        ResponseEntity response = userPreconditionsCheckUtil.checkUserVerification(user);
//        if(response.getStatusCode() != HttpStatus.OK) {
//            return response;
//        }
//
//        if(electionId != null || electionId.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
//                    .body(createResponseUtil.createResponseBody(false, "Election id is empty"));
//        }
//
//        if(!user.getUserElectionsId().contains(electionId)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(createResponseUtil.createResponseBody(false, "This user does not own this election"));
//        }

//        return ResponseEntity.ok().build();

//    }






}
