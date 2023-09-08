package com.ewallet.springbootewallet.repository;


import com.ewallet.springbootewallet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Long> {
    User findByEmailIgnoreCase(String email);
    User findByNameIgnoreCase(String name);
    Boolean existsByEmail(String email);

}


