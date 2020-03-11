package com.main.repo;

import static org.assertj.core.api.Assertions.assertThat;

import com.main.model.Transaction;
import com.main.model.TransactionType;
import com.main.repo.impl.TransactionRepoImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class TransansactionRepoTest {

  private TransactionRepoImpl transactionRepo;

  @Before
  public void setUp() {
    transactionRepo = new TransactionRepoImpl();
  }

  @Test
  public void test_createAndQueryTransaction() {
    long accountId = 10l;
    Transaction transaction = new Transaction(0l, TransactionType.DEPOSIT, BigDecimal.TEN, LocalDateTime.now());

    List<Transaction> transactions = transactionRepo.getTransactionsByAccountId(accountId);
    assertThat(transactions).isEmpty();
    transactionRepo.createTransaction(accountId, transaction);
    transactions = transactionRepo.getTransactionsByAccountId(accountId);
    assertThat(transactions).isNotEmpty().contains(transaction).size().isEqualTo(1);

  }

}
