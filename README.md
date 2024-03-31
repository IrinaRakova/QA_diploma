## Автоматизация тестирования комплексного сервиса, взаимодействующего с СУБД и API Банка
Веб-сервис предлагает купить тур по определённой цене двумя способами: обычная оплата по дебетовой карте и с помощью уникальной технологии выдачи кредита по данным банковской карты. Само приложение не обрабатывает данные по картам, а пересылает их банковским сервисам: сервису платежей, далее Payment Gate; кредитному сервису, далее Credit Gate. 
Заявлена поддержка двух СУБД: MySQL; PostgreSQL. 

В процессе проведения автоматизации была составлена следующая документация:
* [План автоматизации](https://github.com/IrinaRakova/QA_diploma/blob/main/docs/Plan.md):
* [Отчётные документы по итогам тестирования](https://github.com/IrinaRakova/QA_diploma/blob/main/docs/Report.md):
* [Отчётные документы по итогам автоматизации](https://github.com/IrinaRakova/QA_diploma/blob/main/docs/Summary.md):  
## Руководство по запуску SUT:
1.	Запустить Docker.
2.	Запустить контейнеры с помощью команды:
`docker-compose up`
3.	Запустить приложение, путем обращения к необходимой СУБД, с помощью следующих команд:
   
   * Для MySQL: `java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" -jar ./artifacts/aqa-shop.jar`

   *	Для PostgreSQL:`java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar ./artifacts/aqa-shop.jar`

4.	Запустить тесты, путем обращения к необходимой СУБД, с помощью следующих команд:

   * Для MySQL: `./gradlew clean test "-Ddb.url=jdbc:mysql://localhost:3306/app"`

   *	Для PostgreSQL:`./gradlew clean test "-Ddb.url=jdbc:postgresql://localhost:5432/app"`

Для формирования отчета в Allure необходимо ввести в терминале Intellij IDEA `./gradlew allureServe`

Для остановки SUT необходимо в терминале Intellij IDEA, нажать клавиши `CTRL+C`.

Для остановки контейнеров необходимо в терминале Intellij IDEA нажать клавиши `CTRL+C`, ввести в терминале Intellij IDEA команду: `docker-compose down`
