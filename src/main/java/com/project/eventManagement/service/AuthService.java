package com.project.eventManagement.service;

import com.project.eventManagement.dto.request.LoginRequest;
import com.project.eventManagement.dto.request.RegisterRequest;
import com.project.eventManagement.dto.response.TokenResponse;
import com.project.eventManagement.model.User;
import com.project.eventManagement.repository.UserRepository;
import com.project.eventManagement.security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    public User registerUser(RegisterRequest request){
        if (userRepository.existsByUsername(request.getUsername())){
            throw new IllegalArgumentException("Username Already Exists");
        }

        if (userRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("Email Already Exists");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }

    public TokenResponse login(LoginRequest request){
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(()-> new BadCredentialsException("Invalid Username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Invalid password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = tokenProvider.generateToken(userDetails);

        return new TokenResponse(token, user.getUsername(), user.getRole());
    }
}
