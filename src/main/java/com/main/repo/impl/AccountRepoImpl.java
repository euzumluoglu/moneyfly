package com.main.repo.impl;

import com.main.exception.AccountIsLockedException;
import com.main.exception.AccountNotDeletedException;
import com.main.exception.AccountNotFoundException;
import com.main.model.Account;
import com.main.repo.AccountRepo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountRepoImpl implements AccountRepo {

  private AtomicLong counter = new AtomicLong(0);
  private Map<Long, Account> accounts = new ConcurrentHashMap<>();
  private Map<Long, ReentrantLock> accountsLocks = new ConcurrentHashMap<>();

  @Override
  public List<Account> getAccounts() {
    return accounts.values().stream()
        .map(acc -> new Account(acc.getId(), acc.getBalance()))
        .collect(Collectors.toList());
  }

  @Override
  public Account createAccount(BigDecimal balance) {
    long newAccountId = counter.incrementAndGet();
    Account account = new Account(newAccountId, balance);
    accounts.putIfAbsent(newAccountId, account);
    accountsLocks.putIfAbsent(newAccountId, new ReentrantLock());
    return account;
  }

  @SneakyThrows
  @Override
  public void deleteAccount(long id) {
    lockAccount(id);
    Account account = getAccountOrigin(id);
    if (account.getBalance().equals(BigDecimal.ZERO)) {
      accounts.remove(id);
      accountsLocks.get(id).unlock();
      accountsLocks.remove(id);
    } else {
      log.error(String.format("Account %s has some balance and can not be deleted", id));
      throw new AccountNotDeletedException(id);
    }
  }

  @SneakyThrows
  public Account getAccount(long id) {
    Account account = getAccountOrigin(id);
    return new Account(account.getId(), account.getBalance());
  }

  @Override
  public Account getAccountByLock(long id) {
    Account account = getAccountOrigin(id);
    lockAccount(id);
    return new Account(account.getId(), account.getBalance());
  }

  public void updateBalance(long id, BigDecimal newBalance) {
    try {
      lockAccount(id);
      Account account = getAccountOrigin(id);
      account.setBalance(newBalance);
    } finally {
      releaseLock(id);
    }
  }

  public void releaseLock(long id) {
    ReentrantLock accountLock = getAccountLock(id);
    if (accountLock.isHeldByCurrentThread()) {
      accountLock.unlock();
    }
  }

  @SneakyThrows
  private ReentrantLock getAccountLock(long id) {
    ReentrantLock accountLock = accountsLocks.get(id);
    if (accountLock == null) {
      log.error(String.format("Account %s has not been found", id));
      throw new AccountNotFoundException(id);
    }

    return accountLock;
  }

  @SneakyThrows
  private boolean lockAccount(long id) {
    Lock accountLock = getAccountLock(id);
    if (!accountLock.tryLock()) {
      log.error(String.format("Account %s is already locked", id));
      throw new AccountIsLockedException(id);
    }
    return true;
  }

  @SneakyThrows
  private Account getAccountOrigin(long id) {
    Account account = accounts.get(id);
    if (account == null) {
      log.error(String.format("Account %s has not been found", id));
      throw new AccountNotFoundException(id);
    }
    return account;
  }

}
