package com.ewallet.springbootewallet.repository;


import com.ewallet.springbootewallet.domain.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationDao extends JpaRepository<Verification, Long> {
   Verification findByEmail(String email);
}
