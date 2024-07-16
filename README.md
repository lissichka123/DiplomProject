### Запуск автотестов.
1. Склонировать [проект](https://github.com/lissichka123/DiplomProject)
2. Открыть проект в IDEA
3. Для запуска контейнеров использовать команду: docker-compose up -d (MySQL, PostgresSQL, NodeJS)
4. Запустить через терминал SUT возможно с двумя базами данных (MySQL, PostgresSQL), для этого необходимо через терминал ввести команду:
* java -Dspring.datasource.url=jdbc:postgresql://localhost:5432/app -Dspring.datasource.username=app -Dspring.datasource.password=pass -jar artifact/aqa-shop.jar - Для запуска с использованием PostgresSQL
* java -Dspring.datasource.url=jdbc:mysql://localhost:3306/app -Dspring.datasource.username=app -Dspring.datasource.password=pass -jar artifact/aqa-shop.jar - Для запуска с использованием MySQL (Указано по умолчанию, если запускать без параметров будет использовано MySQL)
5. В терминале ввести команду для запусков тестов ./gradlew test (-Dspring.datasource.url=jdbc:mysql://localhost:3306/app или -Dspring.datasource.url=jdbc:postgresql://localhost:5432/app)
6. Посмотреть отчет о тестироовании командой ./gradlew allureServe
