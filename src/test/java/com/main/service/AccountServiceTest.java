package com.main.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.main.model.Account;
import com.main.model.Transaction;
import com.main.model.TransactionType;
import com.main.repo.AccountRepo;
import com.main.repo.TransactionRepo;
import com.main.service.impl.AccountServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

  @Mock
  private AccountRepo accountRepo;

  @Mock
  private TransactionRepo transactionRepo;

  @InjectMocks
  private AccountServiceImpl accountService;

  private List<Account> accounts;

  private List<Transaction> transactions;

  @Before
  public void setUp() {
    accounts = new ArrayList();
    for (long i = 1; i <= 10; i++) {
      accounts.add(new Account(i, BigDecimal.TEN));
    }
    transactions = new ArrayList<>();
    for (long i = 0; i < 10; i++) {
      transactions.add(new Transaction(0l, TransactionType.DEPOSIT, BigDecimal.TEN, LocalDateTime.now()));
    }
  }

  @Test
  public void test_getAccounts() {

    when(accountRepo.getAccounts()).thenReturn(accounts);

    List<Long> accountIds = accountService.getAccounts();

    assertThat(accountIds).size().isEqualTo(accounts.size());
    assertThat(accountIds).isEqualTo(accounts.stream().map(Account::getId).collect(Collectors.toList()));

    verify(accountRepo).getAccounts();

  }

  @Test
  public void test_getAccount() {
    long accountId = 11l;
    Account account = new Account(accountId, BigDecimal.TEN);
    when(accountRepo.getAccount(accountId)).thenReturn(account);

    Account inquiredAccount = accountService.getAccount(accountId);

    assertThat(inquiredAccount).isEqualTo(account);

    verify(accountRepo).getAccount(eq(accountId));
  }

  @Test
  public void test_createAccount() {
    long accountId = 10l;
    Account account = new Account(accountId, BigDecimal.ZERO);
    when(accountRepo.createAccount(eq(BigDecimal.ZERO))).thenReturn(account);

    Account newAccount = accountService.createAccount();

    assertThat(newAccount).isEqualTo(account);

    verify(accountRepo).createAccount(eq(BigDecimal.ZERO));
  }

  @Test
  public void test_getTransactions() {
    long accountId = 10l;
    when(transactionRepo.getTransactionsByAccountId(eq(accountId))).thenReturn(transactions);

    List<Transaction> inquiredTransactions = accountService.getTransactions(accountId);

    assertThat(inquiredTransactions).isEqualTo(transactions);

    verify(transactionRepo).getTransactionsByAccountId(eq(accountId));
  }

  @Test
  public void test_deleteAccount() {
    long accountId = 10l;
    doNothing().when(accountRepo).deleteAccount(eq(accountId));
    accountService.deleteAccount(accountId);
    verify(accountRepo).deleteAccount(eq(accountId));
  }

  @After
  public void tearDown() {
    verifyNoMoreInteractions(accountRepo, transactionRepo);
  }

}
