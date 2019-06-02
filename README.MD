#Spring Todo REST Service

##REST API
 * get Todo by id */v1/todo/{todoId}/user/{userId}* **GET**
 * get Todo list by user */v1/todo/user/{userId}* **GET**
 * set Todo as completed */v1/todo/complete/{todoId}* **GET**
 * create new Todo */v1/todo/user/${userId}* **POST**
 * update Todo */v1/todo/{todoId}/user/${userId}* **PUT**
 
##Run application
 * mvn spring-boot:run 
   
##Tests 
 * Integration test: mvn integration-test

## Build test report (target/site/)
 * mvn surefire-report:report-only
 * mvn surefire-report:failsafe-report-only