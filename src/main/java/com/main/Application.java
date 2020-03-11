package com.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.main.model.Account;
import com.main.service.AccountService;
import com.main.service.TransferService;
import com.main.service.WebServerService;
import java.math.BigDecimal;

public class Application {

  private Injector injector;

  public static void main(String[] args) {

    Application application = new Application();
    application.init();
    application.startWebServer();
  }

  /**
   * sample data creation
   */
  protected void init() {

    injector = Guice.createInjector(new BinderModule());
    final AccountService accountService = injector.getInstance(AccountService.class);
    final TransferService transferService = injector.getInstance(TransferService.class);

    for (int i = 0; i < 100; i++) {
      Account account = accountService.createAccount();
      transferService.depositMoney(account.getId(), new BigDecimal(1000));
    }
  }

  protected void startWebServer() {
    final WebServerService webServerService = injector.getInstance(WebServerService.class);
    webServerService.startWebServer();
  }

  protected void stopWebService() {
    final WebServerService webServerService = injector.getInstance(WebServerService.class);
    webServerService.stopWebServer();
  }

}
