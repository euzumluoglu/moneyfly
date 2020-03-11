package com.main.service;

import java.math.BigDecimal;

public interface TransferService {

  void transferMoney(long fromId, long toId, BigDecimal amount);

  void depositMoney(long id, BigDecimal amount);

  void withdrawMoney(long id, BigDecimal amount);
}
