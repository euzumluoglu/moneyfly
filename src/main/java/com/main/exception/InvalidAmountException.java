package com.main.exception;

public class InvalidAmountException extends RuntimeException {

  public InvalidAmountException() {
    super("Amount can not be below or equal zero");
  }
}
