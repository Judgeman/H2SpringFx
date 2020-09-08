# H2SpringFx
Simple Spring Boot Application with JavaFx and H2 Embedded Database

##### Included Features:

- Spring Boot as basic Application
- JavaFx for the GUI 
   - with Spring Bean creating as Controller Factory
   - Splash Screen
- Logger for writing logs on both console and file
- Embedded H2 Database
- Localization for german and english

#### Test Coverage
For reporting of the test coverage Jacoco is using.
To generate a report use the command in the terminal:
```
mvn test jacoco:report
```
After calling this command the report is generate as HTML page in the location:
> "./target/site/jacoco/"