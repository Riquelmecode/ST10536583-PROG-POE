# ST10536583 - PROG POE - Part 1

Console-based Java application implementing user registration and login,
built with Maven and tested with JUnit 5.

## Features
- Username validation (must contain an underscore, max 5 characters)
- Password complexity validation (min 8 characters, capital letter, number, special character)
- South African cell phone number validation (+27 followed by 9-10 digits)
- Login authentication against registered credentials

## Running
- Console app: run `poe.Main`
- Unit tests: run `mvn test` or use the NetBeans Test action

## References
Regex validation approach adapted from the official Java documentation:
https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
