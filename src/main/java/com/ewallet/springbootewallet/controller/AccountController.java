package com.ewallet.springbootewallet.controller;


import com.ewallet.springbootewallet.Exceptions.AccountNotFoundException;
import com.ewallet.springbootewallet.Exceptions.InsufficientAuthenticationException;
import com.ewallet.springbootewallet.Exceptions.TransactionBadRequest;
import com.ewallet.springbootewallet.domain.Account;
import com.ewallet.springbootewallet.domain.Transaction;
import com.ewallet.springbootewallet.service.AccountService;
import com.ewallet.springbootewallet.service.TransactionService;
import com.ewallet.springbootewallet.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/account")
public class AccountController {

    @Resource
    private AccountService accountService;

    @Resource
    private TransactionService transactionService;

    @GetMapping("/findAccountByAid")
    public Result<Account> findAccountByAidController(@RequestParam long aid) {
        Account newAccount = accountService.findAccountByAidService(aid);
        if (newAccount != null) {
            return Result.success(newAccount, "find account success");
        } else {
            return Result.error("1", "Account not exist");
        }
    }

    @PostMapping("/createAccount")
    public Result<Account> createAccount(@RequestBody Account account) {
        Account newAccount = accountService.createAccountService(account);
        if (newAccount != null) {
            return Result.success(newAccount, "create account success");
        } else {
            return Result.error("1", "Account Already exist");
        }
    }

    @PostMapping("/topup")
    public Result<Transaction> topUpController(@RequestParam Long aid, @RequestParam String accountPassword ,@RequestParam Double amount) {
        try {
            Transaction transactionRecord = accountService.transferToOneService(aid, aid, accountPassword, amount);
            // save transactionRecord to database
            transactionService.addOneTransactionService(transactionRecord);
            return Result.success(transactionRecord, "money top-up success");
        } catch (InsufficientAuthenticationException e) {
            return Result.error("1", "Incorrect password");
        } catch (AccountNotFoundException e) {
            return Result.error("2", "Account not found");
        } catch (TransactionBadRequest e) {
            return Result.error("3", "transaction bad request");
        }
//        if (user != null) {
//            return Result.success(user, "top up success");
//        } else {
//            return Result.error("4", "top up fail");
//        }
    }

    @PostMapping("/transactionToOne")
    public Result<Transaction> transactionToOneController(@RequestParam Long aid, @RequestParam Long receiverAid, @RequestParam String accountPassword, @RequestParam Double amount) {
        Transaction transactionRecord = accountService.transferToOneService(aid, receiverAid, accountPassword, amount);
        Transaction addedTransaction = transactionService.addOneTransactionService((transactionRecord));
        return Result.success(addedTransaction, "transaction success");
    }
}
