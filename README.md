# Simple User & Account Management Module Example Project
REST APIs implemented using Spring Boot Maven Project

## How to Run

* Build the project by running `mvn clean package` inside Account_Management Module
* Once successfully built, run the service by using the following command:
```
java -jar  target/account-1.0.0.jar
```

## REST APIs Endpoints
### Create a User resource
```
POST /api/users
Accept: application/json
Content-Type: application/json

{
"firstName" : "First Name",
"lastName" : "Last Name",
"email": "test@gmail.com",
"dob": "1999-02-11"
}

```

### Update a User resource
```
PUT /api/users/{userId}
Accept: application/json
Content-Type: application/json

{
"id" : "id"
"firstName" : "Firsssssst Name",
"lastName" : "Lastttttt Name",
"email": "test@gmail.com",
"dob": "1999-02-11"

}

```

### Retrieve a list of Users
```
Get /api/users
Accept: application/json
Content-Type: application/json

```

### Find a User Resource
```
Get /api/users/{userId}
Accept: application/json
Content-Type: application/json
```

### Delete a User Resource
```
DELETE /api/users/{userId}
Accept: application/json
Content-Type: application/json
```
```
### Run the tests
To run the Integration tests, execute the following command:
mvn test
