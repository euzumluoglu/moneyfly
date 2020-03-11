package com.main.service;


import com.main.model.Account;
import com.main.model.Transaction;
import java.util.List;

public interface AccountService {

  List<Long> getAccounts();

  Account getAccount(long id);

  Account createAccount();

  void deleteAccount(long id);

  List<Transaction> getTransactions(long accountId);
}
