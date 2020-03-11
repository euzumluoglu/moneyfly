package com.main.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.main.exception.AccountIsLockedException;
import com.main.exception.AccountNotDeletedException;
import com.main.exception.AccountNotFoundException;
import com.main.model.Account;
import com.main.repo.impl.AccountRepoImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

public class AccountRepoTest {

  private AccountRepoImpl accountRepo;

  @Before
  public void setUp() {
    accountRepo = new AccountRepoImpl();
  }

  @Test
  public void test_createAndQueryAccount() {
    Account createdAccount = accountRepo.createAccount(BigDecimal.TEN);
    Account inqueriedAccount = accountRepo.getAccount(createdAccount.getId());

    assertThat(createdAccount).isEqualTo(inqueriedAccount);
  }

  @Test
  public void getAccounts() {
    List<Account> accounts = accountRepo.getAccounts();
    assertThat(accounts).isEmpty();
    Account createdAccount = accountRepo.createAccount(BigDecimal.TEN);
    accounts = accountRepo.getAccounts();
    assertThat(accounts).isNotEmpty().size().isEqualTo(1);
  }

  @Test
  public void test_queryNonExistAccount() {
    try {
      accountRepo.getAccount(1000l);
      fail(String.format("%s exception expected", AccountNotFoundException.class));
    } catch (Exception e) {
      if (!AccountNotFoundException.class.isInstance(e)) {
        fail(String.format("Wrong exception type %s", e.getClass()));
      }
    }
  }

  @Test
  public void test_updateBalance() {
    try {
      accountRepo.updateBalance(1000l, BigDecimal.TEN);
      fail(String.format("%s exception expected", AccountNotFoundException.class));
    } catch (Exception e) {
      if (!AccountNotFoundException.class.isInstance(e)) {
        fail(String.format("Wrong exception type %s", e.getClass()));
      }
    }
  }

  @Test
  public void test_updateBalanceNonExistingAccount() {
    Account createdAccount = accountRepo.createAccount(BigDecimal.ZERO);
    accountRepo.updateBalance(createdAccount.getId(), BigDecimal.TEN);
    Account inquriedAccount = accountRepo.getAccount(createdAccount.getId());
    assertThat(inquriedAccount.getBalance()).isEqualTo(BigDecimal.TEN);

  }

  @Test
  public void test_updateBalanceLockAccount() {
    Account createdAccount = accountRepo.createAccount(BigDecimal.ZERO);
    lockAccount(createdAccount);
    try {
      accountRepo.updateBalance(createdAccount.getId(), BigDecimal.TEN);
      fail(String.format("%s exception expected", AccountIsLockedException.class));
    } catch (Exception e) {
      if (!AccountIsLockedException.class.isInstance(e)) {
        fail(String.format("Wrong exception type %s", e.getClass()));
      }
    }
  }

  @Test
  public void test_deleteNonExistAccount() {
    try {
      accountRepo.deleteAccount(1000l);
      fail(String.format("%s exception expected", AccountNotFoundException.class));
    } catch (Exception e) {
      if (!AccountNotFoundException.class.isInstance(e)) {
        fail(String.format("Wrong exception type %s", e.getClass()));
      }
    }
  }

  @Test
  public void test_deleteLockedAccount() {
    Account createdAccount = accountRepo.createAccount(BigDecimal.ZERO);
    lockAccount(createdAccount);
    try {
      accountRepo.deleteAccount(createdAccount.getId());
      fail(String.format("%s exception expected", AccountIsLockedException.class));
    } catch (Exception e) {
      if (!AccountIsLockedException.class.isInstance(e)) {
        fail(String.format("Wrong exception type %s", e.getClass()));
      }
    }
  }

  @Test
  public void test_deleteBalanceAccount() {

    Account createdAccount = accountRepo.createAccount(BigDecimal.TEN);
    try {
      accountRepo.deleteAccount(createdAccount.getId());
      fail(String.format("%s exception expected", AccountNotDeletedException.class));
    } catch (Exception e) {
      if (!AccountNotDeletedException.class.isInstance(e)) {
        fail(String.format("Wrong exception type %s", e.getClass()));
      }
    }
  }

  @Test
  public void test_deleteAccount() {

    Account createdAccount = accountRepo.createAccount(BigDecimal.ZERO);
    accountRepo.deleteAccount(createdAccount.getId());
    try {
      accountRepo.getAccount(createdAccount.getId());
      fail(String.format("%s exception expected", AccountNotFoundException.class));
    } catch (Exception e) {
      if (!AccountNotFoundException.class.isInstance(e)) {
        fail(String.format("Wrong exception type %s", e.getClass()));
      }
    }
  }

  @SneakyThrows
  private void lockAccount(Account createdAccount) {
    ExecutorService executorService = Executors.newCachedThreadPool();
    Future future = executorService.submit(() -> accountRepo.getAccountByLock(createdAccount.getId()));
    future.get();
    executorService.shutdown();
  }
}
