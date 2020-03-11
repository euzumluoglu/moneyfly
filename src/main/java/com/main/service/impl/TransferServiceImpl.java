package com.main.service.impl;

import static com.main.model.TransactionType.DEPOSIT;
import static com.main.model.TransactionType.INCOMING;
import static com.main.model.TransactionType.OUTGOING;
import static com.main.model.TransactionType.WITHDRAW;

import com.google.inject.Inject;
import com.main.exception.InsufficientBalanceException;
import com.main.exception.InvalidAmountException;
import com.main.model.Account;
import com.main.model.Transaction;
import com.main.repo.AccountRepo;
import com.main.repo.TransactionRepo;
import com.main.service.TransferService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class TransferServiceImpl implements TransferService {

  private final AccountRepo accountRepo;

  private final TransactionRepo transactionRepo;

  @SneakyThrows
  @Override
  public void transferMoney(long fromId, long toId, BigDecimal amount) {

    verifyAmount(amount);
    try {
      Account fromAccount = accountRepo.getAccountByLock(fromId);
      if (fromAccount.getBalance().compareTo(amount) < 0) {
        log.error(String.format("Insufficient balance on on account: %d to transfer account: %d", fromId, toId));
        throw new InsufficientBalanceException(fromId);
      }
      Account toAccount = accountRepo.getAccountByLock(toId);
      BigDecimal fromNewBalance = fromAccount.getBalance().subtract(amount);
      BigDecimal toNewBalance = toAccount.getBalance().add(amount);
      accountRepo.updateBalance(fromAccount.getId(), fromNewBalance);
      accountRepo.updateBalance(toAccount.getId(), toNewBalance);
      LocalDateTime transactionTime = LocalDateTime.now();
      transactionRepo.createTransaction(fromAccount.getId(), new Transaction(toAccount.getId(), OUTGOING, amount, transactionTime));
      transactionRepo.createTransaction(toAccount.getId(), new Transaction(fromAccount.getId(), INCOMING, amount, transactionTime));
    } finally {
      accountRepo.releaseLock(fromId);
      accountRepo.releaseLock(toId);
    }
  }

  private void verifyAmount(BigDecimal amount) {
    if (BigDecimal.ZERO.compareTo(amount) != -1) {
      log.error(String.format("Invalid amount amount:%d ", amount));
      throw new InvalidAmountException();
    }
  }

  /**
   * from id is 0 if money is deposit
   */
  @SneakyThrows
  @Override
  public void depositMoney(long id, BigDecimal amount) {
    verifyAmount(amount);
    try {
      Account account = accountRepo.getAccountByLock(id);
      accountRepo.updateBalance(id, account.getBalance().add(amount));
      transactionRepo.createTransaction(id, new Transaction(0l, DEPOSIT, amount, LocalDateTime.now()));
    } finally {
      accountRepo.releaseLock(id);
    }
  }

  @SneakyThrows
  @Override
  public void withdrawMoney(long id, BigDecimal amount) {
    verifyAmount(amount);
    try {
      Account account = accountRepo.getAccountByLock(id);
      if (account.getBalance().compareTo(amount) < 0) {
        throw new InsufficientBalanceException(id);
      }
      accountRepo.updateBalance(id, account.getBalance().subtract(amount));
      transactionRepo.createTransaction(id, new Transaction(0l, WITHDRAW, amount, LocalDateTime.now()));
    } finally {
      accountRepo.releaseLock(id);
    }
  }
}
