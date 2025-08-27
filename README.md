# Spring Boot Batch Upgrade Demo

This project demonstrates the upgrade process from Spring Boot 2 to Spring Boot 3, along with the corresponding Spring
Batch upgrade from version 4 to 5.

## How To Use This Repo

There are several variants represented in this repo. Each use case has a Spring Boot 2 branch and then a corresponding `-upgrade`
branch that demos the upgrade process to Spring Boot 3.

The easiest way to quickly see the upgrade process is to check the diffs between the non-upgraded branch and the 
upgraded branch. You can do this quickly by selecting and viewing the branch in the GitHub UI and clicking on text at
the top that says "This branch is N commits ahead of main." and looking at the single upgrade commit. 

Alternatively, you can clone the repo and run something along the lines `git diff main upgrade` to see the diff.

## Project Variants

### Simple Variant

* [main branch](https://github.com/JamesMcMahon/spring-boot-demo-batchupgrade/tree/main)
* [upgrade branch](https://github.com/JamesMcMahon/spring-boot-demo-batchupgrade/tree/upgrade)

This simple variant demonstrates a Spring Batch job that processes data in the following flow:

1. Reader - Reads simple "user" input data from a csv file
2. Processor - Transforms the user data by uppercasing both first and last names
3. Writer - Writes transformed data into an H2 database

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

* [databaseless branch](https://github.com/JamesMcMahon/spring-boot-demo-batchupgrade/tree/databaseless)
* [databaseless-upgrade branch](https://github.com/JamesMcMahon/spring-boot-demo-batchupgrade/tree/databaseless-upgrade)

There is also a variant that writes to a file output instead of a database to show the process to get Spring Batch
to work without a database connection.

*In general, I don't recommend this approach. If you don't want to use a database (or don't have access to one),
I recommend replacing the external database dependency with an in-memory H2 database, as seen in the first example.
This keeps things simpler from a bean-overriding perspective.*

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

### Multiple Database Variant

* [multiple-databases branch](https://github.com/JamesMcMahon/spring-boot-demo-batchupgrade/tree/multiple-databases)
* [multiple-databases-upgrade branch](https://github.com/JamesMcMahon/spring-boot-demo-batchupgrade/tree/multiple-databases-upgrade)

There is a more complex version that includes multiple H2 databases
The databases are used for the following:

* Batch Database - stores Spring Batch metadata and manages job execution.
* User Database – stores the processed User data from the writer.
* Audit Database – stores details about the Batch job that could be used for auditing purposes.

*Note: This audit database just for example purposes as Spring Batch already saves this data, so saving it again is
redundant.*

```mermaid
graph LR
    subgraph Job
        A[File Input] --> B[Reader]
        B --> C[Processor]
        C --> D[Writer]
        L[Audit Listener]
    end
    BD[Batch Database] -.-> Job
    D --> F[User Database]
    Job -.-> AD[Batch Database]
    L --> AUD[Audit Database]
    style B fill: #90EE90
    style C fill: #FFB6C1
    style D fill: #87CEEB
    style L fill: #DDA0DD
    style Job fill: #F0F8FF
```

## Useful Resources

* [Spring Batch 5.0 Migration Guide](https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-5.0-Migration-Guide)
* [Spring Boot 3.0 Migration Guide - Batch Changes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide#spring-batch-changes)
