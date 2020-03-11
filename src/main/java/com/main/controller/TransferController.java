package com.main.controller;

import io.javalin.http.Handler;

public interface TransferController {

  Handler transfer();

  Handler depositToAccount();

  Handler withdrawFromAccount();

}
