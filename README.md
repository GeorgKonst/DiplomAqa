# Дипломный проект профессии "Тестировщик"

Дипломный проект представляет собой автоматизацию тестирования комплексного сервиса, взаимодействующего с СУБД и API Банка.

### Планирование автоматизации:

[Plan.md](https://github.com/GeorgKonst/Diplom-AQA/blob/master/docs/Plan.md)


### Отчетные документы по итогам тестирования:

[Report.md](https://github.com/GeorgKonst/DiplomAqa/blob/master/docs/Report.md)


### Отчетные документы по итогам автоматизации:

[Summary.md](https://github.com/GeorgKonst/DiplomAqa/blob/master/docs/Summary.md)


### Запуск приложения:
1. Запустить Docker
2. Загрузить контейнеры mysql, postgres и образ платежного шлюза nodejs в терминале IDEA командой
 
    ````
    docker-compose up
    ````
3. Во втором терминале в папке artifacts запустить SUT командой

   - для конфигурации с базой данный MySql: 
  
      ````
      java -Dspring.datasource.url=jdbc:mysql://localhost/app -jar aqa-shop.jar
      ````
            
   - для конфигурации с базой данных PostgreSQL:
  
       ````
       java -Dspring.datasource.url=jdbc:postgresql://localhost/app -jar aqa-shop.jar
       ```` 
            
4. Запустить автотесты командой 

   -  для конфигурации с MySql
 
      ````
      gradlew test -Dtest.dburl=jdbc:mysql://localhost:3306/app
      ````
            
   - для конфигурации с postgresql
 
      ````
      gradlew test -Dtest.dburl=jdbc:postgresql://localhost:5432/app
      ````
5. Остановить SUT комбдинацией клавиш CTRL+C
6. Остановить контейнеры командой CTRL + C и после удалить контейнеры командой

      ````
      docker-compose down
      ````     
