package com.patti.controllers;


import com.patti.dtos.*;
import com.patti.models.JWTObject;
import com.patti.models.SessionStatus;
import com.patti.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    AuthService authService;

    AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignUpRequestDto request) {
        UserDto userDto =  authService.signup(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request) {
        return authService.login(request.getEmail(), request.getPassword());
    }


    @PostMapping("/validate")
    public ResponseEntity<JWTObject> validateToken(@RequestBody ValidateTokenRequestDto request) {
        JWTObject jwtObject = authService.validate(request.getToken(), request.getUserId());
        return new ResponseEntity<>(jwtObject, HttpStatus.OK);
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request) {
        return authService.logout(request.getToken(), request.getUserId());
    }



}
