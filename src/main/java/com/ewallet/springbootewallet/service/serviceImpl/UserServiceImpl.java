package com.ewallet.springbootewallet.service.serviceImpl;


import com.ewallet.springbootewallet.domain.Confirmation;
import com.ewallet.springbootewallet.domain.User;
import com.ewallet.springbootewallet.domain.Verification;
import com.ewallet.springbootewallet.repository.ConfirmationDao;
import com.ewallet.springbootewallet.repository.UserDao;
import com.ewallet.springbootewallet.repository.VerificationDao;
import com.ewallet.springbootewallet.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private ConfirmationDao confirmationDao;
    @Autowired
    private VerificationDao verificationDao;
    @Autowired
    private EmailServiceImpl emailService;
    @Override
    public User createUser(User user) {
        if (userDao.existsByEmail(user.getEmail())) {
           return null;
        }
        user.setEnabled(false);
        user = userDao.save(user);

        Confirmation confirmation = new Confirmation(user);
        confirmationDao.save(confirmation);

        //emailService.send(user.getName(), user.getEmail(), confirmation.getToken());
        emailService.sendConfirmationToken(user.getName(), user.getEmail(), confirmation.getToken());
        return user;
    }

    @Override
    public Boolean verifyToken(String token) {
        Confirmation confirmation = confirmationDao.findByToken(token);
        if (confirmation == null) {
            return false;
        }
        User user = userDao.findByEmailIgnoreCase(confirmation.getUser().getEmail());
        user.setEnabled(true);
        userDao.save(user);
        confirmationDao.delete(confirmation);
        return true;
    }

    public Boolean resentToken (String email) {
        User user = userDao.findByEmailIgnoreCase(email);
        if (user == null || user.isEnabled()) {
            return false;
        }
        Confirmation confirmation = confirmationDao.findByEmail(email);
        if (confirmation != null) {
            confirmationDao.delete(confirmation);
        }
        confirmation = new Confirmation(user);
        confirmationDao.save(confirmation);
        emailService.sendConfirmationToken(user.getName(), user.getEmail(), confirmation.getToken());
        return true;
    }
    @Override
    public Boolean resetPassword(String email, String code, String password) {
        Verification v = verificationDao.findByEmail(email);
        if (v == null) {
            return false;
        }
        if (!v.getVerificationCode().equals(code)) {
            return false;
        }
        User user = userDao.findByEmailIgnoreCase(email);
        user.setPassword(password);
        userDao.save(user);
        verificationDao.delete(v);
        return true;
    }

    public Boolean sendVerificationCode(String email) {
        Verification v = verificationDao.findByEmail(email);
        if (v != null) {
            verificationDao.delete(v);
        }
        User user = userDao.findByEmailIgnoreCase(email);
        if (user == null) {
            return false;
        }
        v = new Verification(user);
        verificationDao.save(v);
        emailService.sendVerificationCode(user.getName(), user.getEmail(), v.getVerificationCode());
        return true;
    }

    public Boolean validateUser(@RequestBody User user) {
        User u = null;
        if(user.getEmail() != null && user.getEmail().length() > 0) {
            u = userDao.findByEmailIgnoreCase(user.getEmail());
        } else if (user.getName() != null && user.getName().length() > 0) {
            u = userDao.findByNameIgnoreCase(user.getName());
        }

        if (u == null) {
            return false;
        }
        if (!u.getPassword().equals(user.getPassword())) {
            return false;
        }
        if (!u.isEnabled()) {
            return false;
        }
        user.setId(u.getId());
        return true;
    }



    //private FakeUserDao fakeUserDao = new FakeUserDao();

        public String test0() {
        return "test0";
        }
}
