package com.main.exception;

public class InsufficientBalanceException extends RuntimeException {

  public InsufficientBalanceException(long accountId) {
    super(String.format("Account %d does not have enough balance to perform this process", accountId));
  }
}
