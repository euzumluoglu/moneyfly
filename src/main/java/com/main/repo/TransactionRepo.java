package com.main.repo;

import com.main.model.Transaction;
import java.util.List;

public interface TransactionRepo {

  void createTransaction(long accountId, Transaction transaction);

  List<Transaction> getTransactionsByAccountId(long accountId);
}
