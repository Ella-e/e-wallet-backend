# eTreasury
eTreasury is a web application that allows users to manage their finances. It is built using the Springboot framework and intergated the Alipay API to demonstrate the ability to transection with third party payment methods. 

## Features
- User can create an account to the application
- User can need to activate their account by clicking the link sent to their email
- User can login to the application
- User can receive JWT token after login
- User can apply to reset their password by inputting the verification code sent to their email
- User can reset their password after inputting the verification code
- User can create a new wallet bound to their account
- User can view their wallet balance
- User can deposit money to their wallet
- User can make a payment to another user
- User can make a payment to a third party payment method (Alipay)
- User can view their transaction history

We have also implemented a fully functional frontend for this application. Please refer to the following repository for more information: https://github.com/Ella-e/e-wallet-frontend

## Getting Started
### Prerequisites
- Java 17
- Maven, known working version is 3.9.2
- MySQL

### Initial Setup
In the src/main/resources/application.properties file, currently you could see:
```
spring:
    datasource:
        url: jdbc:mysql:///ewalletAccount?useSSL=false&serverTimezone=UTC
        username: root
        password: 1390231mzy
        driver-class-name: com.mysql.cj.jdbc.Driver
```
You need to change the username and password to your own MySQL username and password. And Also make sure to create a database named ewalletAccount in your MySQL.

### Running the Application
The easiest way to run the application is to run it with the in-built/plugin offered by IDEs like IntelliJ.
Or, you can also run using the following command:
```
mvn spring-boot:run
```
### Testing the Application
To test the application, you can use the following command:
```
curl "http://localhost:8080/user/test0"
```
You should see the following output:
```
"test0"
```
The server is running.

### Configuration for Deployment
#### Change the port
In the src/main/resources/application.properties file, currently you could see:
```
server.port=8081
```
you can change the port to any port you want.

#### Change the sender of the verification email
In the src/main/resources/application.properties file, currently you could see:
```
spring:
    mail:
        host: smtp.gmail.com
        port: 587
        username: confirmationemailsender101@gmail.com
        password: ulsejyqnxjwlzpog
        properties:
            mail:
                smtp:
                    auth: true
                    starttls:
                        enable: true
```
You need to change the username and password to your own Gmail username and password. And Also make sure to enable two-step verification and generate an app password for your Gmail account. Put the app password in the password field, not your Gmail password.
#### After deployment
Currently in the src/main/java/com/ewallet/springboot/service/serviceImpl/EmailServiceImpl.java file, you could see:
```
String messageBody = "Hello " + name + ",\n\nYour new account has been created. Please click the link below to verify your account. \n\n" + "http://localhost:8081/user/activate?token=" + token + "\n\nThe support Team";
```
For the "sendConfirmationToken" method. You need to change the "http://localhost:8081/user/activate?token=" to your own domain name. For example, if your domain name is "www.abc.com", you need to change it to "http://www.abc.com/user/activate?token=".

## API Documentation
### User
#### Create a new user
```
POST /user/register
```
Your body should be a JSON object with the following fields:
```
{
    "email": "your email",
    "password": "your password",
    "firstName": "your first name",
    "lastName": "your last name"
}
```
If the email is already bound to a user, you will receive a 400 Bad Request, otherwise, you will receive a 201 Created response, and you will receive an email with the following content:
```
Hello fakeName,

Your new account has been created. Please click the link below to verify your account.

http://localhost:8081/user/activate?token=55790dc5-76de-40e2-a5bf-d3742ba9a3fe

The support Team

```
#### Activate a user
```
GET /user/activate?token=your verification code
```
If the verification code is valid, you will see the following page:
```
tokenVerificationSuccess
```
If the verification code is invalid, you will see the following page:
```
tokenVerificationFailure
```
#### Resent a verification token
```
PUT /user/resent-token?email=your email
```
If the email is bound to a user, you will receive a 200 OK response, othervise, you will receive a 400 Bad Request response with the following body:

#### Apply to reset a user's password (eg: forget password)
```
POST /user/apply-reset-password?email=your email
```
If the email is bound to a user, you will receive a 200 OK response. And you will receive an email with the following content:
```
Hello fakeName,

There is an attempt to reset your password. Verification code is:

z7Ngc6

The support Team

```
If the email is not bound to a user, you will receive a 400 Bad Request response. 

#### Reset a user's password

```
PUT /user/reset-password?email=your email&code=your verification code&password=your new password
```
If the email and verification code are valid, you will receive a 200 OK response and the password will be reset. Otherwise, you will receive a 400 Bad Request response.
#### Login
```
POST /user/login
```
Your body should be a JSON object with the following fields:
```
{
    "email": "your email",
    "name": "your name",
    "password": "your password"
}
```
It is ok to leave one of the email and name fields empty. 
If the email or name and password are valid, you will receive a 200 OK response with the following body:
```
{
    "timeStamp": "2023-09-09T02:42:38.895665",
    "statusCode": 200,
    "status": "OK",
    "message": "login success",
    "data": {
        "email": "e0968880@u.nus.edu",
        "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlMDk2ODg4MEB1Lm51cy5lZHUiLCJleHAiOjE2OTQyMDU3NTh9.P7SoAyRbP0XG7MLwujKz_EEHBlYaE-zrauuzbiXoNqs"
    }
}
```
you need to include the token in the header of your next request to access resources that require authentication. Otherwise, you will receive a 401 unauthorized response.

### Wallet and Transaction
You need to include the token in the header of your request (so you need to first login to get the token) to access resources that require authentication. Otherwise, you will receive a 401 unauthorized response.
#### Create a new wallet
POST /account/createAccount
Your body should be a JSON object with the following fields:
```
{
    "uid": "your user id",
    "accountPassword": "your account password"
}
```

### Get a wallet by wallet id
```
GET /account/findAccountByWid?wid=your wallet id
```
### Get wallets by user id
```
GET /account/findAccountByUid?uid=your user id
```
### Top up a wallet
```
POST /account/topup?aid=your wallet id&accountPassword=your account password&amount=your amount
```
### Transfer money to another wallet
```
POST /account/transactionToOne?aid=your wallet id&receiverAid=receiver wallet id&accountPassword=your account password&amount=your amount
```
### Transfer money to a third party payment method (Alipay)
You need to include the token in the header of your request (so you need to first login to get the token) to access resources that require authentication. Otherwise, you will receive a 401 unauthorized response.
#### Create a new Alipay transaction
```
POST /api/alipay/createWebTrade?tradeNo=your trade number&subject=your subject&totalAmount=your total amount
```
#### Get a Alipay transaction by trade number
```
GET /api/alipay/findWebTradeByTradeNo?tradeNo=your trade number
```
### Transection
You need to include the token in the header of your request (so you need to first login to get the token) to access resources that require authentication. Otherwise, you will receive a 401 unauthorized response.
#### Make a transaction
```
POST /transaction/transact
```
Your body should be a JSON object with the following fields:
```
{
    "fromAccountId": "your wallet id",
    "toAccountId": "receiver wallet id",
    "amount": "your amount"
}
```
#### Get transactions by fromAccountId
```
GET /transaction/findTransactionByFromId?fromAccountId=your wallet id
```
#### Get transactions by toAccountId
```
GET /transaction/findTransactionByToId?toAccountId=your wallet id
```
#### Add a transaction
```
POST /transaction/addOneTransaction
```
Your body should be a JSON object with the following fields:
```
{
    "fromAccountId": "your wallet id",
    "toAccountId": "receiver wallet id",
    "amount": "your amount"
}
```
## Alipay API Integration

### Project Overview

The Alipay payment integration in this project is just a small part of the entire application. It primarily involves the integration of the Alipay SDK's API, allowing users to initiate payment requests to the Alipay gateway. It also leverages Alipay's sandbox environment for virtual account transfers. Furthermore, you will need Alipay's public and private keys, personal public and private keys, and support for functionalities such as synchronous/asynchronous notifications, refunds, and queries.

### Key Features

- **Virtual Accounts**: By using virtual accounts within Alipay's sandbox environment, you can simulate payment transactions to ensure the correctness and stability of the payment process.

- **Key Management**: Managing multiple keys is crucial in this integration, including Alipay's public and private keys and personal public and private keys. These keys are used for signing and encrypting transaction data.

- **Synchronous/Asynchronous Notifications**: After a payment is completed, users can receive both synchronous and asynchronous notifications to promptly update order information when payment statuses change. 

You will need to configure the return and notify URLs for synchronous and asynchronous notifications. These URLs should point to the appropriate endpoints in your application for handling Alipay notifications.

Synchronous Notification URL: Your Synchronous Notification URL Here
Asynchronous Notification URL: Your Asynchronous Notification URL Here

- **Refund and Query (API Interfaces)**: The refund and query functionalities are provided as API interfaces. You can use these interfaces to initiate refund requests and query payment transaction statuses. However, please note that the actual implementation of these features is pending.

