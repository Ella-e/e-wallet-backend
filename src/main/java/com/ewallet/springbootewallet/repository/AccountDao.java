package com.ewallet.springbootewallet.repository;


import com.ewallet.springbootewallet.domain.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountDao extends JpaRepository<Account, Long> {

    // createAccount can be done by the save function provided by jpa

    Account findAccountByUid(Long uid);

    Account findAccountByAid(Long aid);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account SET balance=:balance WHERE aid=:aid", nativeQuery = true)
    Integer updateBalance(@Param("aid") Long aid, @Param("balance") Double balance);

//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE account SET balance=:balance WHERE uid=:uid AND aid=:aid", nativeQuery = true)
//    Integer updateBalance(@Param("uid") Long uid, @Param("aid") Long aid, @Param("balance") Double balance);
}
