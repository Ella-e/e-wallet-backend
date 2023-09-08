package com.ewallet.springbootewallet.service;


import com.ewallet.springbootewallet.domain.User;

public interface UserService {
    User createUser(User user);
    Boolean verifyToken(String token);
    Boolean resentToken (String email);
    Boolean resetPassword(String email, String code, String password);
    Boolean sendVerificationCode(String email);
    Boolean validateUser(User user);
    String test0();
}
