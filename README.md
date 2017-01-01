# Rura Serv

Для запуска сервера
```
mvn jetty:run -Dwicket.configuration=development
```
Для обработки frontend'а
```
mvn frontend:gulp -Dfrontend.gulp.arguments=watch
```
Сервер запускается по адресу: [http://localhost:8080](http://localhost:8080)

Файлы конфигурации:

 - RuraConstants.java - `src\main\java\ru\ruranobe\wicket\RuraConstans.java`
 - RuraConfig.xml - `src\main\java\ru\ruranobe\config\RuraConfig.xml`
 - RuraConfig.xsd - `src\main\java\ru\ruranobe\config\RuraConfig.xsd`
 - gulpfile.js - `./gulpfile.js`
 - pom.xml - `./pom.xml`

> By [ruranobe.ru](http://ruranobe.ru/).