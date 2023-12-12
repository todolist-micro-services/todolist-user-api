package com.funnyproject.todolistuserapi.user;

import com.funnyproject.todolistuserapi.AppConfig;
import com.funnyproject.todolistuserapi.dto.UserDto;
import com.funnyproject.todolistuserapi.utils.InitDataInterface;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.database.DataInterface;
import todolist.database.dataType.User;

@RestController
@RequestMapping("/users")
public class RetrieveUser {

    private final AppConfig appConfig;
    private final DataInterface dataInterface;

    public RetrieveUser(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(), appConfig.getDbPassword());
    }

    @GetMapping("/me")
    public ResponseEntity<Object> retrieveUser(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, HttpServletRequest request) {
        if (authorizationHeader == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Bad authorization header\"}");
        UserDto user = new UserDto();
        final String[] authorization = authorizationHeader.split(" ");

        if (authorization.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Bad authorization header\"}");
        }
        User databaseUser = this.dataInterface.getUserFromToken(authorization[1]);
        if (databaseUser == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        user.setEmail(databaseUser.email);
        user.setFirstname(databaseUser.firstname);
        user.setLastname(databaseUser.lastname);
        user.setId(databaseUser.userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
