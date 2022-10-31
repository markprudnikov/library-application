# Library Application Task

### Example of usage

In the project folder:
```
$ gradle build; ./gradlew run
```
In the second terminal:
```
$ curl localhost:8080/api/v1.0/books/
[]
$ curl -X PUT localhost:8080/api/v1.0/books -H 'Content-Type: application/json' -d '{"id":1, "title":"Harry Potter", "author":"", "description":"", "isbn":"ISBN-FASD", "printYear": 1998, "readAlready": false}'
1
$ curl localhost:8080/api/v1.0/books/
[{"id":1,"title":"Harry Potter","description":"","author":"","isbn":"ISBN-FASD","printYear":1998,"readAlready":false}]
```
