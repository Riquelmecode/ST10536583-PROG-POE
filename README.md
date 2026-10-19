# ST10536583 - PROG POE - Part 2 (QuickChat)

Console-based Java application that builds on Part 1 (registration and login)
by adding QuickChat messaging. Built with Maven and tested with JUnit 5.

## Features
### From Part 1
- Username validation (must contain an underscore, max 5 characters)
- Password complexity validation (min 8 characters, capital letter, number, special character)
- South African cell phone number validation (+27 followed by 9-10 digits)
- Login authentication against registered credentials

### New in Part 2
- Messaging is only available after a successful login
- "Welcome to QuickChat." message and a numeric menu:
  1) Send Messages, 2) Show recently sent messages ("Coming Soon."), 3) Quit
- The user chooses how many messages to enter, and can only enter that many
- Each message gets a random ten-digit ID, a message number, a validated recipient and a hash
  (first two digits of the ID, message number, first and last words in caps, e.g. `00:0:HITONIGHT`)
- Messages are limited to 250 characters
- Each message can be sent, disregarded or stored for later
- Stored messages are saved to `stored_messages.json`
- Sent message details are shown (Message ID, Message Hash, Recipient, Message)
  and the total number of messages sent is shown at the end

## Running
- Console app: run `poe.Main` (or `mvn compile exec:java`)
- Unit tests: run `mvn test` or use the NetBeans Test action

## Continuous Integration
The GitHub Actions workflow in `.github/workflows/maven.yml` runs `mvn test`
on every push and pull request.

## References
Regex validation approach adapted from the official Java documentation:
https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html

JSON storage uses Google Gson:
https://github.com/google/gson/blob/main/UserGuide.md

GitHub Actions Maven workflow based on GitHub's documentation:
https://docs.github.com/en/actions/use-cases-and-examples/building-and-testing/building-and-testing-java-with-maven
