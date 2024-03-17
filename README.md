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
3.	Запустить приложение с помощью команды:
`java -jar ./artifacts/aqa-shop.jar`
4.	Запустить тесты, путем обращения к необходимой СУБД с помощью следующих команд:

   * Для MySQL: `./gradlew -Durlbd=jdbc:mysql://localhost:3306/app clean test`

   *	Для PostgreSQL:`./gradlew -Durlbd=jdbc:postgresql://localhost:5432/app clean test`

Для формирования отчета в Allure необходимо ввести в терминале Intellij IDEA `./gradlew allureServe`

При необходимости отключения SUT, находясь в терминале Intellij IDEA, нажать клавиши `CTRL+C`.

При необходимости отключения контейнеров в терминале Intellij IDEA нажать клавиши `CTRL+C`, ввести в терминале Intellij IDEA команду: `docker-compose down`
