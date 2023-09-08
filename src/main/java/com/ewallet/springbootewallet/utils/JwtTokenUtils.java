package com.ewallet.springbootewallet.utils;


import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.ewallet.springbootewallet.domain.User;
import com.ewallet.springbootewallet.repository.UserDao;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Component
public class JwtTokenUtils {
    public static UserDao staticUserDao;
    @Resource
    UserDao userDao;

    @PostConstruct
    public void init() {
        staticUserDao = userDao;
    }

    public static String generateToken(String email, String sign) {
        return JWT.create().withAudience(email)
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2))
                .sign(Algorithm.HMAC256(sign));
    }

    public static User getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (StringUtils.isNotBlank(token)) {
                String email = JWT.decode(token).getAudience().get(0);
                return staticUserDao.findByEmailIgnoreCase(email);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static boolean validateToken(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        String email;
        try {
            email = JWT.decode(token).getAudience().get(0);
        } catch (Exception e) {
            return false;
        }
        User user = staticUserDao.findByEmailIgnoreCase(email);
        if (user == null) {
            return false;
        }

        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
