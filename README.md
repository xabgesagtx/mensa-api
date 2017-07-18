
# Mensa API

[![Build Status](https://travis-ci.org/xabgesagtx/mensa-api.svg?branch=master)](https://travis-ci.org/xabgesagtx/mensa-api)

This project aims to provide the means to create an API for all data about mensas in Hamburg

## Features

* indexing of all menus for the mensas in Hamburg
* REST-API with Swagger documentation and Swagger UI view
* responsive HTML view of menus
* GraphQL-API with GraphiQL view 
* access of menus via Telegram bot

## Components

There are the following components

* *mensa-common* provides the shared code infrastructure, especially for persistence (i.e. MongoDB)
* *mensa-http* provides HTTP access to the mensa data
* *mensa-indexer* provides scheduled indexing of the mensa content
* *mensa-telegram* provides a Telegram bot for accessing the mensa data

Except of the the mensa-common component are all components Spring Boot applications.

To compile the project run `./gradlew build`

Afterwards you can execute the compiled jar files right away.

## Configuration

For configuration you can provide an application.yml file next to each jar file. The most important configuration would be the configuration of the MongoDB connection. You should set this connection details for all applications equally.

```
spring:
  data:
    mongodb:
      database: DB_NAME
      host: HOSTNAME
      username: USERNAME
      password: PASSWORD
```

### HTTP

You might want to set the port where the http API should be running
```
server:
  port: 8080
```

### Telegram

For telegram usage you need to set the name of your bot and its token
```
telegram:
  botname: NAME
  token: TOKEN

```

## Requirements

* MongoDB
* Java 8