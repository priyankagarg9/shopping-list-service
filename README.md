# shopping-list-service

As its name suggests, this service is responsible to manage a list of items for grocery shopping. The application must expose a REST API. It should contain endpoints to:
   
 - Create an item with the following properties:
   - ID (generated automatically)
   - Name (case sensitive)
   - Quantity (set to 1 by default if not passed)
   - Comment (optional)
   
  - Gets a specific item by id (response in JSON Object format)
  - Get a list of all items
  - Updates an item
  - Deletes an item

#### Restrictions

 - The `name` field is unique and mandatory
 - The quantity cannot be less than 1
 
# Solution README

#### Build
 - Run `mvn clean package` in the `shopping-list-service` directory where the pom.xml resides.
 - A war will be created in the target folder `shopping-list-service/target`

#### Deployment
 - Download Apache Tomcat(http://mirror.23media.de/apache/tomcat/tomcat-8/v8.5.30/bin/) and unpackage it into a tomcat folder
 - Copy our WAR file from target/shopping-list-service.war to the tomcat/webapps/ folder
 - From a terminal navigate to tomcat/bin folder and execute
 - catalina.bat run (on Windows)
 - catalina.sh run (on Unix-based systems)
 - Verify that the shopping-list-service is up by hitting [`shopping-list-service`](http://localhost:8080/shopping-list-service/swagger-ui.html#/)
 - To stop the application - ctrl + c
 
 
#### Testing
 - There are validations for `name` field 
   - It should not be null or empty
   - It should be unique
   
 For testing the API : 
* Click on POST /v1/shopping-list.
* Click on Try it out.
* Copy paste the shopping item object from below cases and click on Execute.

##### Case 1:
```
- Shopping Item : Valid.

{
  "comment": "Try another brand",
  "name": "Sugar",
  "quantity": 1
}
```
```
- Response - HTTP code 201

{
  "id": 1,
  "name": "Sugar",
  "comment": "Try another brand",
  "quantity": 1
} 
```

##### Case 2:
```
- Shopping Item : Name null.

{
  "comment": "Try another brand",
  "name": null,
  "quantity": 1
}
```
```
- Response - HTTP code 400

[
  {
    "message": "must not be blank",
    "field": "name"
  }
]
```
##### Case 3:
```
- Shopping Item: Already exists

{
  "comment": "Try another brand",
  "name": "Sugar",
  "quantity": 1
}
```
```
- Response - HTTP code 400

{
  "message": "Shopping item already exists. Try updating the quantity for the item Sugar",
  "status": "400 BAD_REQUEST"
}
```

##### Case 4:
```
- Shopping item - without passing quantity (will be set to 1 by default)

{
  "comment": "Search for low cholesterol",
  "name": "Oil",
  "quantity": 1
}
```
```
- Response

{
  "id": 2,
  "name": "Oil",
  "comment": "Search for low cholesterol",
  "quantity": 1
}
```
##### Case 5:
```
- Shopping Item - Quantity less than 1
{
  "comment": "Bring penne pasta this time",
  "name": "Pasta",
  "quantity": 0
}
```
```
- Response - HTTP code 400

[
  {
    "message": "must be greater than or equal to 1",
    "field": "quantity"
  }
]
```

 - Update the objects created above using the endpoint: PUT/v1/shopping-list/{id}
 - See all the objects using the endpoint: GET /v1/shopping-list
 - See a specific item details using the endpoint: GET /v1/shopping-list/{id}
 - Delete any item from the list using the endpoint: DELETE /v1/shopping-list/{id}
 
 #### First level architecture design and assumptions:
* The application is using h2 in-memory database
* The provided solutions takes into account all the mentioned assumptions in the code challenge
* For real systems, the code will be distributed in different layers like validators, mappers etc.
* For real systems, logs should be written in logs files.
* For real systems, separate constant files should be created and used across layer to avoid code repetition

 
