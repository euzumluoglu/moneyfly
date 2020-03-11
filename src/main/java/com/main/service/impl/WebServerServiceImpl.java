package com.main.service.impl;

import com.google.inject.Inject;
import com.main.controller.AccountController;
import com.main.controller.TransferController;
import com.main.exception.AccountIsLockedException;
import com.main.exception.AccountNotDeletedException;
import com.main.exception.AccountNotFoundException;
import com.main.exception.InsufficientBalanceException;
import com.main.exception.InvalidAmountException;
import com.main.exception.handler.ExceptionHandlerImpl;
import com.main.service.WebServerService;
import io.javalin.Javalin;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class WebServerServiceImpl implements WebServerService {

  private final AccountController accountController;

  private final TransferController transferController;

  private final ExceptionHandlerImpl exceptionHandler;

  private Javalin app;

  public void startWebServer() {

    app = Javalin.create(config -> {
      config.defaultContentType = "application/json";
    }).start(9090);
    app.post("/accounts", accountController.createAccount());
    app.get("/accounts", accountController.fetchAllAccounts());
    app.get("/accounts/:id", accountController.fetchAccount());
    app.delete("/accounts/:id", accountController.deleteAccount());
    app.get("/accounts/:id/transactions", accountController.fetchTransactions());
    app.post("/transfers", transferController.transfer());
    app.post("/deposits", transferController.depositToAccount());
    app.post("/withdraws", transferController.withdrawFromAccount());

    app.exception(AccountNotFoundException.class, exceptionHandler.getAccountNotFoundHandler());
    app.exception(AccountIsLockedException.class, exceptionHandler.getAccountIsLockException());
    app.exception(AccountNotDeletedException.class, exceptionHandler.getGeneralExceptionHandler());
    app.exception(InsufficientBalanceException.class, exceptionHandler.getGeneralExceptionHandler());
    app.exception(InvalidAmountException.class, exceptionHandler.getGeneralExceptionHandler());
  }

  public void stopWebServer() {
    if (app != null) {
      app.stop();
    }
  }
}
