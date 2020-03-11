package com.main;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.main.controller.AccountController;
import com.main.controller.TransferController;
import com.main.controller.impl.AccountControllerImpl;
import com.main.controller.impl.TransferControllerImpl;
import com.main.repo.AccountRepo;
import com.main.repo.TransactionRepo;
import com.main.repo.impl.AccountRepoImpl;
import com.main.repo.impl.TransactionRepoImpl;
import com.main.service.AccountService;
import com.main.service.TransferService;
import com.main.service.WebServerService;
import com.main.service.impl.AccountServiceImpl;
import com.main.service.impl.TransferServiceImpl;
import com.main.service.impl.WebServerServiceImpl;

public class BinderModule extends AbstractModule {


  @Override
  protected void configure() {

    bind(AccountRepo.class).to(AccountRepoImpl.class).in(Singleton.class);
    bind(TransactionRepo.class).to(TransactionRepoImpl.class).in(Singleton.class);
    bind(AccountService.class).to(AccountServiceImpl.class).in(Singleton.class);
    bind(TransferService.class).to(TransferServiceImpl.class).in(Singleton.class);
    bind(AccountController.class).to(AccountControllerImpl.class).in(Singleton.class);
    bind(TransferController.class).to(TransferControllerImpl.class).in(Singleton.class);
    bind(WebServerService.class).to(WebServerServiceImpl.class).in(Singleton.class);

  }

}