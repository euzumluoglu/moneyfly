package com.main.service.impl;

import com.google.inject.Inject;
import com.main.model.Account;
import com.main.model.Transaction;
import com.main.repo.AccountRepo;
import com.main.repo.TransactionRepo;
import com.main.service.AccountService;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class AccountServiceImpl implements AccountService {

  private final AccountRepo accountRepo;
  private final TransactionRepo transactionRepo;

  @Override
  public List<Long> getAccounts() {
    List<Account> accounts = accountRepo.getAccounts();
    return accounts.stream().map(account -> account.getId()).collect(Collectors.toList());
  }

  @Override
  public Account getAccount(long id) {
    return accountRepo.getAccount(id);
  }

  @Override
  public Account createAccount() {
    return accountRepo.createAccount(BigDecimal.ZERO);
  }

  @Override
  public List<Transaction> getTransactions(long accountId) {
    return transactionRepo.getTransactionsByAccountId(accountId);
  }

  @Override
  public void deleteAccount(long id) {
    accountRepo.deleteAccount(id);
  }
}
