package com.ewallet.springbootewallet.service;

import com.ewallet.springbootewallet.Exceptions.AccountNotFoundException;
import com.ewallet.springbootewallet.Exceptions.InsufficientAuthenticationException;
import com.ewallet.springbootewallet.domain.Account;
import com.ewallet.springbootewallet.domain.Transaction;
import jakarta.persistence.NonUniqueResultException;

import java.util.List;

public interface AccountService {
    Account createAccountService(Account account);

    Account findAccountByUidService(Long uid) throws NonUniqueResultException;

    Account findAccountByAidService(Long aid);

    Integer transferOutService(Long uid, String password, Double amount) throws Exception;

    Integer receiveService(Long uid, Double amount) throws Exception;

    Transaction transferToOneService(Long aid, Long receiverAid, String password, Double amount) throws InsufficientAuthenticationException, AccountNotFoundException;
}
