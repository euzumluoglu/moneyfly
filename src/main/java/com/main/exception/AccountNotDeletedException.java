package com.main.exception;

public class AccountNotDeletedException extends RuntimeException {

  public AccountNotDeletedException(long accountId) {
    super(String.format("Account %d has balance and can not be deleted", accountId));
  }
}
