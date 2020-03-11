package com.main.exception;

public class AccountNotFoundException extends RuntimeException {

  public AccountNotFoundException(long accountId) {
    super(String.format("Account %d not found on the System", accountId));
  }
}
