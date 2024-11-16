package com.tesinitsyn.userservice.service;

import com.tesinitsyn.userservice.dto.ReqRes;
import com.tesinitsyn.userservice.messaging.RabbitMQProducer;
import com.tesinitsyn.userservice.models.MyUser;
import com.tesinitsyn.userservice.repository.MyUserRepository;
import com.tesinitsyn.userservice.utils.JWTUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthService {

    private final MyUserRepository myUserRepository;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RabbitMQProducer rabbitMQProducer;

    public AuthService(MyUserRepository myUserRepository, JWTUtils jwtUtils, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, RabbitMQProducer rabbitMQProducer) {
        this.myUserRepository = myUserRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    public ReqRes signUp(ReqRes registrationRequest){
        ReqRes resp = new ReqRes();
        try {
            MyUser myUser = new MyUser();
            myUser.setEmail(registrationRequest.getEmail());
            myUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            myUser.setRole(registrationRequest.getRole());
            MyUser myUserResult = myUserRepository.save(myUser);
            if (myUserResult.getId()>0) {
                resp.setMyUser(myUserResult);
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }
        }catch (Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes signIn(ReqRes signInRequest){
        ReqRes response = new ReqRes();
        try {
            System.out.printf("EMAIL: %s, PASSWORD: %s\n", signInRequest.getEmail(), signInRequest.getPassword());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(),signInRequest.getPassword()));
            var user = myUserRepository.findByEmail(signInRequest.getEmail()).orElseThrow();
            System.out.println("USER IS: "+ user);
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Signed In");
            rabbitMQProducer.sendMessage( signInRequest.getEmail());

        }catch (Exception e){
            response.setStatusCode(500);
            response.setError(e.getMessage());
            System.out.println("ERROR: " + e.getMessage());
            System.out.println("ERROR: " + e);

        }
        return response;
    }

    public ReqRes refreshToken(ReqRes refreshTokenReqiest){
        ReqRes response = new ReqRes();
        String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
        MyUser users = myUserRepository.findByEmail(ourEmail).orElseThrow();
        if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
            var jwt = jwtUtils.generateToken(users);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenReqiest.getToken());
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Refreshed Token");
        }
        response.setStatusCode(500);
        return response;
    }
}
