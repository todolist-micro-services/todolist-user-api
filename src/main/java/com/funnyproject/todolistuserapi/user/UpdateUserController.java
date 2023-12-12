package com.funnyproject.todolistuserapi.user;

import com.funnyproject.todolistuserapi.AppConfig;
import com.funnyproject.todolistuserapi.dto.UserDto;
import com.funnyproject.todolistuserapi.utils.InitDataInterface;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;
import todolist.database.dataType.User;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UpdateUserController {

    private final DataInterface dataInterface;

    public UpdateUserController(AppConfig appConfig) {
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(), appConfig.getDbPassword());
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateUser(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            HttpServletRequest request,
            @RequestBody UpdateUserRequest updateUserRequest
    ) {
        final String[] authorization = authorizationHeader.split(" ");
        ResponseEntity<Object> checkBodyError = this.checkBody(updateUserRequest);
        User databaseUser;

        if (checkBodyError != null)
            return checkBodyError;
        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Bad authorization header\"}");
        }
        databaseUser = this.dataInterface.getUserFromToken(authorization[1]);
        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        if (!this.dataInterface.updateUser(authorization[1], new User(0, updateUserRequest.getFirstname(), updateUserRequest.getLastname(), databaseUser.email, databaseUser.password)).isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal server error\"}");
        return this.returnNewUser(authorization[1]);
    }

    private ResponseEntity<Object> returnNewUser(final String token) {
        UserDto user = new UserDto();
        User databaseUser = this.dataInterface.getUserFromToken(token);

        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        user.setEmail(databaseUser.email);
        user.setFirstname(databaseUser.firstname);
        user.setLastname(databaseUser.lastname);
        user.setId(databaseUser.userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private ResponseEntity<Object> checkBody(UpdateUserRequest updateUserRequest) {
        try {
            this.validateUpdateRequest(updateUserRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("{\"error\": \"Missing parameters, needs : firstname, lastname\"}");
        }
        return null;
    }

    private void validateUpdateRequest(UpdateUserRequest updateUserRequest) {
        if (updateUserRequest == null || updateUserRequest.getFirstname() == null || updateUserRequest.getLastname() == null)
            throw new IllegalArgumentException("Missing required parameters");
    }

}
