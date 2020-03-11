package com.main.repo;

import com.main.model.Account;
import java.math.BigDecimal;
import java.util.List;


public interface AccountRepo {

  List<Account> getAccounts();

  Account getAccount(long id);

  Account getAccountByLock(long id);

  void updateBalance(long id, BigDecimal newBalance);

  void releaseLock(long id);

  Account createAccount(BigDecimal balance);

  void deleteAccount(long id);

}
