package com.main.controller;

import io.javalin.http.Handler;

public interface AccountController {

  Handler fetchAllAccounts();

  Handler fetchAccount();

  Handler createAccount();

  Handler deleteAccount();

  Handler fetchTransactions();

}
