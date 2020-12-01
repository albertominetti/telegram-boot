# telegram-boot

This application contains the behaviour of a simple bot for Telegram, made with Java 11, Spring and spring-boot.

## Foreword

It is not the first time I develop a bot for Telegram, the very first one has been done in `php` around 2010, then I did it again with `Node.js` and finally one with `Java` around 2015. All these bots worked as expected but the code has always been very though to maintain, mainly because
1. every new feature become an additional `if` in the main procedural code
2. the inner status for each chat has been added in the final stage of the application, more like a workaround instead of a design choice. 

![The Bot Father](the_bot_father.jpg)

## Challenges and ToDo list

- [x] bot features using dependency injection
    - [x] external API integration
    - [x] a single message can trigger multiple replies
    - [ ] watch external events to trigger a reply in chat
    - [x] external API integration
    - [ ] quiz feature with scores
- [x] handle multiple chats in parallel
- [x] allow to use the polling strategy or the wekbooh registration to interact with the Telegram API
- [x] multi-language support
- [x] proper handling of the inner status
    - [x] handle database versoning with liquibase
- [x] require a minimal configuration for the deployment on the cloud
    - [x] deployment on Heroku as spring-boot app
    - [x] deployment on DigitalOcean App as Docker image
- [ ] administration web page

## Requirements

You need [Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html), a [Telegram bot token](https://core.telegram.org/bots#3-how-do-i-create-a-bot) and a database. The database is optional because the app can run with an inner H2 but it will lose the status after a reboot.

# Software architecture overview

Note: I am going to use **message** as synonym of the [Telegram Update](https://core.telegram.org/bots/api#update) object that technically contains the [Message](https://core.telegram.org/bots/api#message), because I consider that in each Update there is a message, and it is a text message.

The best way to describe the design of the application is to start from the flow when a new message arrives. The message arrives to the `UpdateEntryPoint` that has no actual logic of message handling, but it will pass the responsiblity of handling to a specific `Feature`. It has a list of `Feature` as collaborators, each `Feature` has a boolean method `test` to check if it can handle the message and the actual `process` method to handle. Moreover the UpdateEntryPoint retrieves from the database the `ChatInfo` for the specific message. It contains the language, the current status, and other additional info. Every `Feature` works using the message itself, the `ChatInfo` and, eventually, retrieving some additional data from the database or from external services (API). If no `Feature` can handle the message, the `UpdateEntryPoint` informs the user in the chat. 

`UpdateEntryPoint`, `Feature` and `ChatInfo` are Java classes in my code, take a look to have a better oveview.

## Polling vs Webhook

The current implementation uses a [polling mechanism](https://core.telegram.org/bots/api#getupdates) instead of the [webhook registration](https://core.telegram.org/bots/api#setwebhook). I feel more comfortable with the first one for several reasons:
* it is much more easy to test locally
* a single polling can retrieve multiple messages
* it should avoid potential DoS when many available messages
* a message that generates an exception it is immediately discarged without retries (can be done also with the webhook, but with the polling mechanism is more immediate)
* (?) multiple services can poll the Telegram API in the same time

There are also few point in favor of the webhook registration:
* load balancing using a reverse proxy (like NGINX or Apache), of course not needed for my hobby project
* when a message needs a long processing time, the other messages do not wait for it, but it requires a better handling of web workers
* a single HTTP call that hold the chat message as HTTP request and the bot reply as HTTP response, so faster if you need one single reply

In the current implementation, the polling mechanism allow the parallel processing of the messages thank to a BlockingQueue and to a Scheduler that runs in a thread that is separed from the web workers and other threads run the actual message processing.

### To be continued...
