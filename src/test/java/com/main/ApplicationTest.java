package com.main;

import static org.assertj.core.api.Assertions.assertThat;

import com.main.model.Account;
import com.main.model.Cashflow;
import com.main.model.Transaction;
import com.main.model.Transfer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTest {

  private static Application application = new Application();
  private String url = "http://localhost:9090";
  private int transferCount = 1000;
  private Long accountIdFirst = 1l;
  private Long accountIdSecond = 2l;
  private Long accountIdThird = 3l;
  private Long accountIdFourth = 4l;

  @BeforeClass
  public static void init() {
    application.init();
    application.startWebServer();
  }

  @AfterClass
  public static void cleanUp() {
    application.stopWebService();
  }

  @Test
  public void test_1_createAccount() {
    HttpResponse<Account> response = Unirest.post(url + "/accounts").asObject(Account.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED_201);
    assertThat(response.getBody().getBalance()).isEqualTo(BigDecimal.ZERO);
  }

  @Test
  public void test_2_deleteAccount() {

    long deleteAccountId = 100l;
    /**
     * Account has balance and can not be deleted
     */
    HttpResponse response = Unirest.delete(url + "/accounts/" + deleteAccountId).asObject(String.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);

    /**
     * successfull account deletion
     */
    Account noBalanceAccount = createAccount();
    response = Unirest.delete(url + "/accounts/" + noBalanceAccount.getId()).asObject(String.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);

    /**
     * Account is not exist
     */
    response = Unirest.delete(url + "/accounts/" + noBalanceAccount.getId()).asObject(String.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND_404);
  }

  @Test
  public void test_3_getAccounts() {
    HttpResponse<List<Integer>> response = Unirest.get(url + "/accounts/").asObject(new GenericType<List<Integer>>() {
    });
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);
    assertThat(response.getBody()).size().isGreaterThan(4);
  }

  @Test
  public void test_4_getAccountDetails() {
    HttpResponse response = Unirest.get(url + "/accounts/" + accountIdFourth).asObject(Account.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);
    assertThat(response.getBody().toString()).isEqualTo(new Account(accountIdFourth, new BigDecimal(1000)).toString());

    response = Unirest.get(url + "/accounts/" + 10000).asObject(Account.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND_404);
  }

  @Test
  public void test_5_getTransactions() {
    ObjectMapper om = Unirest.config().getObjectMapper();
    configureMapper();

    HttpResponse<Transaction[]> responseFirst = Unirest.get(url + "/accounts/" + accountIdFirst + "/transactions")
        .asObject(Transaction[].class);
    Assertions.assertThat(responseFirst.getStatus()).isEqualTo(HttpStatus.OK_200);
    Assertions.assertThat(responseFirst.getBody()).isNotEmpty().hasAtLeastOneElementOfType(Transaction.class);

    Unirest.config().setObjectMapper(om);

  }

  @Test
  public void test_6_makeTransfer() throws ExecutionException, InterruptedException {
    ExecutorService executorService = Executors.newCachedThreadPool();
    Transfer transfer1 = new Transfer(accountIdFirst, accountIdSecond, BigDecimal.ONE);
    Transfer transfer2 = new Transfer(accountIdSecond, accountIdThird, BigDecimal.ONE);
    Transfer transfer3 = new Transfer(accountIdThird, accountIdFirst, BigDecimal.ONE);

    List<Future> responses = new ArrayList<>();
    int created = 0;
    int conflict = 0;

    for (int i = 0; i < transferCount; i++) {
      responses.add(executorService.submit(() -> makeTransfer(transfer1)));
      responses.add(executorService.submit(() -> makeTransfer(transfer2)));
      responses.add(executorService.submit(() -> makeTransfer(transfer3)));
    }
    for (Future future : responses) {
      int response = (int) future.get();
      if (response == HttpStatus.CREATED_201) {
        created++;
      } else if (response == HttpStatus.CONFLICT_409) {
        conflict++;
      }
    }
    executorService.shutdown();
    assertThat(responses.size()).isEqualTo(created + conflict);
    assertThat(conflict).isGreaterThan(0);
  }

  @Test
  public void test_7_makeDeposit() {

    /**
     * successful money deposit
     */
    Cashflow cashflow = new Cashflow(accountIdFirst, BigDecimal.valueOf(100l));
    HttpResponse response = Unirest.post(url + "/deposits").body(cashflow).asObject(String.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED_201);

    /**
     * invalid request
     */
    cashflow = new Cashflow(accountIdFirst, BigDecimal.valueOf(100l).negate());
    response = Unirest.post(url + "/deposits").body(cashflow).asObject(String.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);

    /**
     * account not exist
     */
    cashflow = new Cashflow(10000l, BigDecimal.valueOf(100l));
    response = Unirest.post(url + "/deposits").body(cashflow).asObject(String.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND_404);
  }

  @Test
  public void test_7_withDrawMoney() {

    /**
     * successful money withdraw
     */
    Cashflow cashflow = new Cashflow(accountIdFirst, BigDecimal.valueOf(100l));
    HttpResponse response = Unirest.post(url + "/withdraws").body(cashflow).asObject(String.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED_201);

    /**
     * insufficient balance
     */
    cashflow = new Cashflow(accountIdFirst, BigDecimal.valueOf(100000l));
    response = Unirest.post(url + "/withdraw").body(cashflow).asObject(String.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);

    /**
     * invalid request
     */
    cashflow = new Cashflow(accountIdFirst, BigDecimal.valueOf(100l).negate());
    response = Unirest.post(url + "/withdraw").body(cashflow).asObject(String.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);

    /**
     * account not exist
     */
    cashflow = new Cashflow(10000l, BigDecimal.valueOf(100l));
    response = Unirest.post(url + "/withdraw").body(cashflow).asObject(String.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND_404);
  }

  private Account createAccount() {
    HttpResponse<Account> response = Unirest.post(url + "/accounts").asObject(Account.class);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED_201);
    return response.getBody();
  }

  private void configureMapper() {
    Unirest.config().getObjectMapper();
    Unirest.config().setObjectMapper(new ObjectMapper() {
      com.fasterxml.jackson.databind.ObjectMapper mapper
          = new com.fasterxml.jackson.databind.ObjectMapper();

      @SneakyThrows
      @Override
      public <T> T readValue(String value, Class<T> valueType) {
        return mapper.readValue(value, valueType);
      }

      @SneakyThrows
      @Override
      public String writeValue(Object value) {
        return mapper.writeValueAsString(value);
      }
    }).getClient();
  }

  private int makeTransfer(Transfer transfer) {
    HttpResponse response = Unirest.post(url + "/transfers").body(transfer).asObject(String.class);
    return response.getStatus();

  }
}
