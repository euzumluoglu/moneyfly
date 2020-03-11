package com.main.exception;

public class AccountIsLockedException extends RuntimeException {

  public AccountIsLockedException(long accountId) {
    super(String.format("Account %d is busy with another process. Please try again later", accountId));
  }
}
