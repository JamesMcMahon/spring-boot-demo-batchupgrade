# Spring Boot Batch Upgrade Demo

This project demonstrates the upgrade process from Spring Boot 2 to Spring Boot 3, along with the corresponding Spring
Batch upgrade from version 4 to 5.

## Project Structure

This project demonstrates a Spring Batch job that processes data in the following flow:

1. Reader - Reads simple "user" input data from a csv file
2. Processor - Transforms the user data by uppercasing both first and last names
3. Writer - Writes transformed data into a database

```mermaid
graph LR
    A[File Input] --> B[Reader]
    B --> C[Processor]
    C --> D[Writer]
    D --> E[Database]
    style B fill: #90EE90
    style C fill: #FFB6C1
    style D fill: #87CEEB
```

### Databaseless Variant

There is also a variant that writes to a file output instead of a database to show up the process to get Spring Batch
to work without a database connection.

```mermaid
graph LR
    A[File Input] --> B[Reader]
    B --> C[Processor]
    C --> D[Writer]
    D --> E[File Output]
    style B fill: #90EE90
    style C fill: #FFB6C1
    style D fill: #87CEEB
```

## Branches

Each branch represents a specific configuration to help you understand the upgrade process and implementation options.

* `main` - Spring Boot 2.x with Spring Batch 4.x (Pre-upgrade version with database)
* `upgrade` - Spring Boot 3.x with Spring Batch 5.x (Post-upgrade version with database)
* `databaseless` - Spring Boot 2.x without database dependency (File-based output)
* `databaseless-upgrade` - Spring Boot 3.x without database dependency (File-based output)

## Useful Resources

* [Spring Batch 5.0 Migration Guide](https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-5.0-Migration-Guide)
* [Spring Boot 3.0 Migration Guide - Batch Changes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide#spring-batch-changes)
