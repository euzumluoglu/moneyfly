package com.main.controller.impl;

import com.google.inject.Inject;
import com.main.controller.AccountController;
import com.main.service.AccountService;
import io.javalin.http.Handler;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.http.HttpStatus;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class AccountControllerImpl implements AccountController {

  private final AccountService accountService;

  @Override
  public Handler fetchAllAccounts() {
    return ctx -> {
      ctx.status(HttpStatus.OK_200);
      ctx.json(accountService.getAccounts());
    };
  }

  @Override
  public Handler fetchAccount() {
    return ctx -> {
      ctx.status(HttpStatus.OK_200);
      ctx.json(accountService.getAccount(Long.parseLong(Objects.requireNonNull(ctx.pathParam("id")))));
    };
  }

  @Override
  public Handler createAccount() {
    return ctx -> {
      ctx.status(HttpStatus.CREATED_201);
      ctx.json(accountService.createAccount());
    };
  }

  @Override
  public Handler deleteAccount() {
    return ctx -> {
      accountService.deleteAccount(Long.parseLong(Objects.requireNonNull(ctx.pathParam("id"))));
      ctx.status(HttpStatus.OK_200);
    };
  }

  @Override
  public Handler fetchTransactions() {
    return ctx -> {
      ctx.status(HttpStatus.OK_200);
      ctx.json(accountService.getTransactions(Long.parseLong(Objects.requireNonNull(ctx.pathParam("id")))));
    };
  }


}
