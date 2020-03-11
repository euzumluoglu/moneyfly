package com.main.controller.impl;

import com.google.inject.Inject;
import com.main.controller.TransferController;
import com.main.model.Cashflow;
import com.main.model.Transfer;
import com.main.service.TransferService;
import io.javalin.http.Handler;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.http.HttpStatus;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class TransferControllerImpl implements TransferController {

  private final TransferService transferService;

  @Override
  public Handler transfer() {
    return ctx -> {
      Transfer transfer = ctx.bodyAsClass(Transfer.class);
      transferService.transferMoney(transfer.getFromId(), transfer.getToId(), transfer.getAmount());
      ctx.status(HttpStatus.CREATED_201);
    };
  }

  @Override
  public Handler depositToAccount() {
    return ctx -> {
      Cashflow cashflow = ctx.bodyAsClass(Cashflow.class);
      transferService.depositMoney(cashflow.getId(), cashflow.getAmount());
      ctx.status(HttpStatus.CREATED_201);
    };
  }

  @Override
  public Handler withdrawFromAccount() {
    return ctx -> {
      Cashflow cashflow = ctx.bodyAsClass(Cashflow.class);
      transferService.withdrawMoney(cashflow.getId(), cashflow.getAmount());
      ctx.status(HttpStatus.CREATED_201);
    };
  }
}