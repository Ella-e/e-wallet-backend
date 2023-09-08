package com.ewallet.springbootewallet.repository;


import com.ewallet.springbootewallet.domain.Confirmation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationDao extends JpaRepository <Confirmation, Long> {
    Confirmation findByToken(String token);
    Confirmation findByEmail(String email);
}
