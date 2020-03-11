package com.main.exception.handler;

import io.javalin.http.ExceptionHandler;
import org.eclipse.jetty.http.HttpStatus;

public class ExceptionHandlerImpl {

  public ExceptionHandler<Exception> getGeneralExceptionHandler() {
    return (e, ctx) -> {
      ctx.status(HttpStatus.BAD_REQUEST_400);
      ctx.result(e.toString());
    };
  }

  public ExceptionHandler<Exception> getAccountNotFoundHandler() {
    return (e, ctx) -> {
      ctx.status(HttpStatus.NOT_FOUND_404);
      ctx.result(e.toString());
    };
  }

  public ExceptionHandler<Exception> getAccountIsLockException() {
    return (e, ctx) -> {
      ctx.status(HttpStatus.CONFLICT_409);
      ctx.result(e.toString());
    };
  }
}
