package com.project.eventManagement.controller;

import com.project.eventManagement.dto.request.LoginRequest;
import com.project.eventManagement.dto.request.RegisterRequest;
import com.project.eventManagement.dto.response.TokenResponse;
import com.project.eventManagement.model.User;
import com.project.eventManagement.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private  final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request){
        User user = authService.registerUser(request);
        return new ResponseEntity<>("User Registered", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public  ResponseEntity<TokenResponse> login (@RequestBody LoginRequest request){
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
