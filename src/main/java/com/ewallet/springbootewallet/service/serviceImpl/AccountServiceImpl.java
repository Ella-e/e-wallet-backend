package com.ewallet.springbootewallet.service.serviceImpl;

import com.ewallet.springbootewallet.Exceptions.AccountNotFoundException;
import com.ewallet.springbootewallet.Exceptions.InsufficientAuthenticationException;
import com.ewallet.springbootewallet.Exceptions.TransactionBadRequest;
import com.ewallet.springbootewallet.domain.Account;
import com.ewallet.springbootewallet.domain.Transaction;
import com.ewallet.springbootewallet.repository.AccountDao;
import com.ewallet.springbootewallet.repository.UserDao;
import com.ewallet.springbootewallet.service.AccountService;
import com.ewallet.springbootewallet.utils.TimeUtil;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
public class AccountServiceImpl implements AccountService {
    @Resource
    private AccountDao accountDao;

    @Resource
    private UserDao userDao;

    @Override
    public Account createAccountService(Account account) {
        Account existedAccount = findAccountByUidService(account.getUid());
        if (existedAccount != null) {
            return null;
        }
        Account newAccount = accountDao.save(account);
        return newAccount;
    }

    @Override
    public Account findAccountByUidService(Long uid) {
        return null;
    }

    @Override
    public Account findAccountByAidService(Long aid) {
        Account account = accountDao.findAccountByAid(aid);
        return account;
    }

    @Transactional
    @Override
    public Integer transferOutService(Long aid, String accountPassword, Double amount) throws TransactionBadRequest, AccountNotFoundException{
        Account account = accountDao.findAccountByAid(aid);
        if (account == null) {
            throw new AccountNotFoundException(aid);
        }
        Double currentBalance = account.getBalance();
        //TODO: also add in frontend validation
        if (amount < 0) {
            throw new TransactionBadRequest();
        }
//        if (!account.getAccountPassword().equals(accountPassword)) {
//            // password not right
//            throw new InsufficientAuthenticationException();
//        }
        if (amount <= currentBalance) {
            // sufficient balance in account, can proceed
            Double newBalance = currentBalance - amount;
            Integer status = accountDao.updateBalance(aid, newBalance);
            return status;
        } else {
            throw new TransactionBadRequest();
        }
    }

    @Override
    public Integer receiveService(Long aid, Double amount) throws AccountNotFoundException{
        Account account = accountDao.findAccountByAid(aid);
        if (account == null) {
            throw new AccountNotFoundException(aid);
        }
        Double newBalance = account.getBalance() + amount;
        Integer status =accountDao.updateBalance(aid, newBalance);
        return status;
    }

    @Transactional
    @Override
    public Transaction transferToOneService(Long aid, Long receiverAid, String password, Double amount) throws InsufficientAuthenticationException, TransactionBadRequest, AccountNotFoundException{
        Account account = accountDao.findAccountByAid(aid);
        String type = "transfer";
        String status = "";
        if (!account.getAccountPassword().equals(password)) {
            // password not right
            throw new InsufficientAuthenticationException();
        }
        if (aid.equals(receiverAid)) {
        // perform top-up
            status = receiveService(receiverAid, amount).toString();
            type = "topup";
        } else {
        // perform transaction
            status = transferOutService(aid, password, amount).toString();
            status = status + receiveService(receiverAid, amount).toString();
        }
        // Generate transaction history
        Transaction transaction = Transaction.build(aid, receiverAid, type, amount, "", status, TimeUtil.getTime());
        return transaction;
    }
}
