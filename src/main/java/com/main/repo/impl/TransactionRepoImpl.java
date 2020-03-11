package com.main.repo.impl;

import com.main.model.Transaction;
import com.main.repo.TransactionRepo;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.jetbrains.annotations.NotNull;

public class TransactionRepoImpl implements TransactionRepo {

  private Map<Long, Vector<Transaction>> transactions = new HashMap<>();

  public void createTransaction(long accountId, Transaction transaction) {
    Vector<Transaction> transactions = getTransactionsByAccId(accountId);
    transactions.add(transaction);

  }

  @Override
  public List<Transaction> getTransactionsByAccountId(long accountId) {
    Vector<Transaction> transactions = getTransactionsByAccId(accountId);
    return Collections.list(transactions.elements());
  }

  @NotNull
  private Vector<Transaction> getTransactionsByAccId(long accountId) {
    Vector<Transaction> transactions = this.transactions.get(accountId);
    if (transactions == null) {
      transactions = new Vector<>();
      this.transactions.put(accountId, transactions);
    }
    return transactions;
  }


}
