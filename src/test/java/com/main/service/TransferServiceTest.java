package com.main.service;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.main.exception.AccountIsLockedException;
import com.main.exception.InsufficientBalanceException;
import com.main.exception.InvalidAmountException;
import com.main.model.Account;
import com.main.model.Transaction;
import com.main.repo.AccountRepo;
import com.main.repo.TransactionRepo;
import com.main.service.impl.TransferServiceImpl;
import java.math.BigDecimal;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TransferServiceTest {

  @Mock
  private AccountRepo accountRepo;

  @Mock
  private TransactionRepo transactionRepo;

  @InjectMocks
  private TransferServiceImpl transferService;

  @Test
  public void test_invalidBalance() {
    balanceRequest(0l);
    balanceRequest(-1l);
  }

  @Test
  public void test_moneyTransferInsufficientBalance() {
    when(accountRepo.getAccountByLock(anyLong()))
        .then(invocation -> new Account(invocation.getArgument(0), BigDecimal.ZERO));

    try {
      transferService.transferMoney(1l, 2l, BigDecimal.TEN);
      fail(String.format("%s exception expected", InsufficientBalanceException.class));
    } catch (Exception e) {
      if (!InsufficientBalanceException.class.isInstance(e)) {
        fail(String.format("Wrong exception type %s", e.getClass()));
      }
    }

    verify(accountRepo).getAccountByLock(anyLong());
    verify(accountRepo, times(2)).releaseLock(anyLong());
  }

  @Test
  public void test_moneyTransferAccountIsLocked() {
    when(accountRepo.getAccountByLock(anyLong()))
        .then(invocation -> {
          throw new AccountIsLockedException(invocation.getArgument(0));
        });
    try {
      transferService.transferMoney(1l, 2l, BigDecimal.TEN);
      fail(String.format("%s exception expected", AccountIsLockedException.class));
    } catch (Exception e) {
      if (!AccountIsLockedException.class.isInstance(e)) {
        fail(String.format("Wrong exception type %s", e.getClass()));
      }
    }

    verify(accountRepo).getAccountByLock(anyLong());
    verify(accountRepo, times(2)).releaseLock(anyLong());
  }

  @Test
  public void test_successfulMoneyTransfer() {

    long fromId = 1l;
    long toId = 2l;

    when(accountRepo.getAccountByLock(eq(fromId)))
        .thenReturn(new Account(fromId, BigDecimal.TEN));

    when(accountRepo.getAccountByLock(eq(toId)))
        .thenReturn(new Account(toId, BigDecimal.ONE));

    doNothing().when(transactionRepo).createTransaction(eq(fromId), any(Transaction.class));
    doNothing().when(transactionRepo).createTransaction(eq(toId), any(Transaction.class));

    transferService.transferMoney(fromId, toId, BigDecimal.TEN);

    verify(accountRepo).getAccountByLock(eq(fromId));
    verify(accountRepo).getAccountByLock(eq(toId));
    verify(accountRepo).updateBalance(eq(fromId), any(BigDecimal.class));
    verify(accountRepo).updateBalance(eq(toId), any(BigDecimal.class));
    verify(accountRepo).releaseLock(eq(fromId));
    verify(accountRepo).releaseLock(eq(toId));
    verify(transactionRepo).createTransaction(eq(toId), any(Transaction.class));
    verify(transactionRepo).createTransaction(eq(fromId), any(Transaction.class));
  }

  @Test
  public void test_successfulDeposit() {
    long accountId = 1l;

    when(accountRepo.getAccountByLock(eq(accountId)))
        .thenReturn(new Account(accountId, BigDecimal.TEN));
    doNothing().when(transactionRepo).createTransaction(eq(accountId), any(Transaction.class));

    transferService.depositMoney(accountId, BigDecimal.TEN);

    verify(accountRepo).getAccountByLock(eq(accountId));
    verify(accountRepo).updateBalance(eq(accountId), any(BigDecimal.class));
    verify(accountRepo).releaseLock(eq(accountId));
    verify(transactionRepo).createTransaction(eq(accountId), any(Transaction.class));
  }

  @Test
  public void test_withDepositMoneyAccountIsLocked() {
    when(accountRepo.getAccountByLock(anyLong()))
        .then(invocation -> {
          throw new AccountIsLockedException(invocation.getArgument(0));
        });
    try {
      transferService.depositMoney(1l, BigDecimal.TEN);
      fail(String.format("%s exception expected", AccountIsLockedException.class));
    } catch (Exception e) {
      if (!AccountIsLockedException.class.isInstance(e)) {
        fail(String.format("Wrong exception type %s", e.getClass()));
      }
    }

    verify(accountRepo).getAccountByLock(anyLong());
    verify(accountRepo).releaseLock(anyLong());
  }

  @Test
  public void test_successfulWithDraw() {
    long accountId = 1l;

    when(accountRepo.getAccountByLock(eq(accountId)))
        .thenReturn(new Account(accountId, BigDecimal.TEN));
    doNothing().when(transactionRepo).createTransaction(eq(accountId), any(Transaction.class));

    transferService.withdrawMoney(accountId, BigDecimal.TEN);

    verify(accountRepo).getAccountByLock(eq(accountId));
    verify(accountRepo).updateBalance(eq(accountId), any(BigDecimal.class));
    verify(accountRepo).releaseLock(eq(accountId));
    verify(transactionRepo).createTransaction(eq(accountId), any(Transaction.class));
  }

  @Test
  public void test_withWithdrawMoneyAccountIsLocked() {
    when(accountRepo.getAccountByLock(anyLong()))
        .then(invocation -> {
          throw new AccountIsLockedException(invocation.getArgument(0));
        });
    try {
      transferService.withdrawMoney(1l, BigDecimal.TEN);
      fail(String.format("%s exception expected", AccountIsLockedException.class));
    } catch (Exception e) {
      if (!AccountIsLockedException.class.isInstance(e)) {
        fail(String.format("Wrong exception type %s", e.getClass()));
      }
    }

    verify(accountRepo).getAccountByLock(anyLong());
    verify(accountRepo).releaseLock(anyLong());
  }

  @Test
  public void test_withWithdrawMoneyInsufficientBalance() {
    long accountId = 1l;

    when(accountRepo.getAccountByLock(eq(accountId)))
        .thenReturn(new Account(accountId, BigDecimal.ONE));
    try {
      transferService.withdrawMoney(1l, BigDecimal.TEN);
      fail(String.format("%s exception expected", InsufficientBalanceException.class));
    } catch (Exception e) {
      if (!InsufficientBalanceException.class.isInstance(e)) {
        fail(String.format("Wrong exception type %s", e.getClass()));
      }
    }

    verify(accountRepo).getAccountByLock(anyLong());
    verify(accountRepo).releaseLock(anyLong());
  }

  private void balanceRequest(long balance) {
    try {
      transferService.transferMoney(1l, 2l, BigDecimal.valueOf(balance));
      fail(String.format("%s exception expected", InvalidAmountException.class));
    } catch (Exception e) {
      if (!InvalidAmountException.class.isInstance(e)) {
        fail(String.format("Wrong exception type %s", e.getClass()));
      }
    }
  }

  @After
  public void tearDown() {
    verifyNoMoreInteractions(accountRepo, transactionRepo);
  }
}
