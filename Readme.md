# Moneyfly

Moneyfly is a money transfer platform.

  - Create,delete and query account
  - Money transfer, withdraw and deposit
  - init() method creates accounts with id from 1 -to 100 with balance 1000

### Endpoints
  - post("/accounts")
  - get("/accounts")
  - get("/accounts/:id")
  - delete("/accounts/:id")
  - get("/accounts/:id/transactions")
  - post("/transfers")
  - post("/deposits")
  - post("/withdraws")

### How to run
Go inside the forder and run command below in console. 
It will start on **port 9090**.
```sh
$ gradlew run
```

### Sample calls
post("/accounts")
```sh
$ curl --location --request POST 'localhost:9090/accounts' 
```
get("/accounts/1")
```sh
$ curl --location --request GET 'localhost:9090/accounts/1' 
```

delete("/accounts/1")
```sh
$ curl --location --request DELETE 'localhost:9090/accounts/1' 
```

get("/accounts")
```sh
$ curl --location --request GET 'localhost:9090/accounts' 
```

get("/accounts/1/transactions")
```sh
$ curl --location --request GET 'localhost:9090/accounts/1/transactions' 
```

post("/withdraws")
```sh
$ curl --location --request POST 'localhost:9090/withdraws' \
--header 'Content-Type: application/json' \
--data-raw '{
	"id" : 1,
	"amount" : "1.00"
	
}'
```

post("/deposits")
```sh
$ curl --location --request POST 'localhost:9090/deposits' \
--header 'Content-Type: application/json' \
--data-raw '{
	"id" : 1,
	"amount" : "1.00"
	
}'
```

post("/transfers")
```sh
$ curl --location --request POST 'localhost:9090/transfers' \
--header 'Content-Type: application/json' \
--data-raw '{
	"fromId" : 1,
	"toId" : 2,
	"amount" : "1.00"
}'
```

