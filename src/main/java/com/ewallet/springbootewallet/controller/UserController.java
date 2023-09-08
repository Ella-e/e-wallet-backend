package com.ewallet.springbootewallet.controller;


import com.ewallet.springbootewallet.domain.HttpResponse;
import com.ewallet.springbootewallet.domain.User;
import com.ewallet.springbootewallet.security.AuthAccess;
import com.ewallet.springbootewallet.service.EmailService;
import com.ewallet.springbootewallet.service.UserService;
import com.ewallet.springbootewallet.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    @AuthAccess
    public ResponseEntity<HttpResponse> createUser(@RequestBody User user) {
        User newUser = userService.createUser(user);
        if(newUser == null) {
            return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .message("Email already bound to user")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }

        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", newUser))
                        .message("User created, please check your email for confirmation to activate your account")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()

        );
    }
    @AuthAccess
    @PutMapping("/resent-token")
    public ResponseEntity<HttpResponse> resentToken(@RequestParam("email") String email) {

        Boolean isSuccess = userService.resentToken(email);
        if (!isSuccess) {
            return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("Success", isSuccess))
                            .message("email not found")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("verification code sent")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }
    @AuthAccess
    @GetMapping("/activate")
    public String confirmUser(@RequestParam("token") String token, Model model) {
        Boolean isSuccess = userService.verifyToken(token);
        if (isSuccess) {
            return "tokenVerificationSuccess";
        } else {
            return "tokenVerificationFailure";
        }
    }
    @AuthAccess
    @PutMapping("/reset-password")
    public ResponseEntity<HttpResponse> resetPassword(@RequestParam("email") String email,
                              @RequestParam("code") String code,
                              @RequestParam("password") String password) {
        Boolean isSuccess = userService.resetPassword(email, code, password);
        if (!isSuccess) {
            return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("Success", isSuccess))
                            .message("invalid email or code")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("Success", isSuccess))
                        .message("password reset")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );

    }
    @AuthAccess
    @PostMapping("apply-reset-password")
    public ResponseEntity<HttpResponse> applyResetPassword(@RequestParam("email") String email) {

        Boolean isSuccess = userService.sendVerificationCode(email);
        if (!isSuccess) {
            return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .data(Map.of("Success", isSuccess))
                            .message("email not found")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("verification code sent")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }
    @AuthAccess
    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody User user) {
        if (userService.validateUser(user)) {
            String token = JwtTokenUtils.generateToken(user.getEmail(), user.getPassword());
            return ResponseEntity.ok().body (
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .message("login success")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(Map.of("token", token,"email", user.getEmail()))
                            .build()
            );
        } else {
            return ResponseEntity.badRequest().body(
                    HttpResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .message("login failed")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    public void logout() {
    }

    public void changeEmail() {
    }
    @AuthAccess
    @GetMapping("/test0")
    public String test0() {
       return "test0";
    }

    @GetMapping("/test1")
    public String test1() {
       return "test1";
    }
}
