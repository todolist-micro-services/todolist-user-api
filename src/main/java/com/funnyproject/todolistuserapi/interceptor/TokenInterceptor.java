package com.funnyproject.todolistuserapi.interceptor;

import com.funnyproject.todolistuserapi.AppConfig;
import com.funnyproject.todolistuserapi.utils.InitDataInterface;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import todolist.database.DataInterface;
import todolist.database.dataType.Token;

import java.time.LocalDateTime;


public class TokenInterceptor implements HandlerInterceptor {

    private final AppConfig appConfig;
    private final DataInterface dataInterface;

    public TokenInterceptor(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.dataInterface = InitDataInterface.initDataInterface(appConfig.getDbUrl(), appConfig.getDbUserName(), appConfig.getDbPassword());
    }

    private String getToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || authorizationHeader.split(" ").length != 2)
            return null;
        return authorizationHeader.split(" ")[1];
    }

    private boolean formatErrorResponse(HttpServletResponse response, final String message) throws Exception {
        String errorMessage = String.format("{\"error\": \"%s\"}", message);

        response.getWriter().write(errorMessage);
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return false;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        final String token = this.getToken(request);
        if (token == null)
            return this.formatErrorResponse(response, "You need a token");
        Token dbToken = this.dataInterface.getUserTokenFromToken(token);
        if (dbToken == null)
            return this.formatErrorResponse(response, "Token expired");
        if (dbToken.expirationDate.isBefore(LocalDateTime.now())) {
            this.dataInterface.deleteUserToken(dbToken.user);
            return this.formatErrorResponse(response, "Token expired");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
    }
}

