## To setup OpenAI's API

- add in the root of the project (i.e., the same level where `pom.xml` is located) a file named `apiproxy.config`

  ```
  email: "your_email@example.com"
  apiKey: "YOUR_KEY"
  ```
  these are your credentials to invoke the OpenAI GPT APIs

## To setup codestyle's API

- add in the root of the project (i.e., the same level where `pom.xml` is located) a file named `codestyle.config`

  ```
  email: "your_email@example.com"
  accessToken: “YOUR_KEY”
  ```

## To run the game

`./mvnw clean javafx:run`

## To debug the game

`./mvnw clean javafx:run@debug` then in VS Code "Run & Debug", then run "Debug JavaFX"

## To run codestyle

`./mvnw clean compile exec:java@style`
