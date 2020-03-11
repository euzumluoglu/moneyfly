package com.main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionType {

  INCOMING("TRANSFER"),
  OUTGOING("TRANSFER"),
  DEPOSIT("DEPOSIT"),
  WITHDRAW("WITHDRAW");

  private String value;
}
