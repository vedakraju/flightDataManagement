#  Flight Data Management Service

The **Flight Data Management Service** is a Spring Boot microservice 
that provides a RESTful API for managing flight data. It supports 
creation, update, deletion, and search operations on flight records 
stored in a PostgreSQL database. The service uses Liquibase for database 
schema management and supports time-zone aware flight scheduling.

---
## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Configuration](#configuration)
  - [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
  - [Get All Flights](#get-all-flights)
  - [Create a Flight](#create-a-flight)
  - [Update a Flight](#update-a-flight)
  - [Delete a Flight](#delete-a-flight)
  - [Search Flights](#search-flights)
- [Date-Time Format](#date-time-format)
- [Database Connection (Optional)](#database-connection-optional)

## Features

- Create, update, delete flight records
- Search flights with multiple optional filters
- Handles time-zone-aware searches
- Uses PostgreSQL and Liquibase
- Logs incoming requests for audit

## Technologies Used

- Java 17
- Spring Boot
- PostgreSQL
- Liquibase
- Maven
- Lombok
- SLF4J (logging)

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL installed locally
- PgAdmin (optional)
- IntelliJ IDEA or any preferred IDE

### Configuration
1. Create a database named `flightData_db` in PostgreSQL.
2. Update `application.properties`:
update the properties with your data source url,username and password 
below fields 
spring.datasource.url=jdbc:postgresql://localhost:5432/flightData_db
spring.datasource.username=`USER_NAME`
spring.datasource.password=`PASSWORD`
3. Running the application will create the required schema, table and constraints with the help of liquibase configurations mentioned in changelog-master.xml
4. Make sure to enter the data uniquely as we have the unique constraint `unique_flight_key` on the column airline, departure_airport, departure_time.
5. As The domain api.crazy-supplier.com Does not exist currently when ever the search for the supplier CrazySupplier is made it is returning one mocked data.
   external.api.unavailable=true from the application.properties tells that we will fetch the mocked data

### Running the Application
Run the main class: FlightDataManagementServiceApplication
Service will be available at:http://localhost:8080/flight
For Authentication select Auth Type : Basic Auth 
Username : user 
Password : d412d6b5-c5da-4762-8879-637a918cd0b3 [Password generated in the console you will find the text like Using generated security password: d412d6b5-c5da-4762-8879-637a918cd0b3 in console]

## API Endpoints
All endpoints are prefixed with /flight
### [Get All Flights](#get-all-flights)
  GET http://localhost:8080/flight
  Response:
  [
    {
    "flightId": 1,
    "airline": "Lufthansa",
    "supplier": "CrazySupplier",
    "departureAirport": "FRA",
    "destinationAirport": "JFK",
    "departureTime": "2025-06-14T05:00:00Z",
    "arrivalTime": "2025-06-14T15:00:00Z",
    "price": 500.00
    }
  ]
### [Create a Flight](#create-a-flight)
  POST http://localhost:8080/flight
  Request Body:
  {
  "airline": "Lufthansa",
  "supplier": "CrazySupplier",
  "departureAirport": "FRA",
  "destinationAirport": "JFK",
  "departureTime": "2025-06-14T05:00:00Z", // ISO_DATE_TIME format
  "arrivalTime": "2025-06-14T15:00:00Z",
  "price": 500.00
  }

### [Update a Flight](#update-a-flight)
  PATCH http://localhost:8080/flight
  Request Body: {
  "id":1,
  "airline": "KingFisher",
  "supplier": "Amadeus",
  "departureAirport": "FRA",
  "destinationAirport": "JFK",
  "departureTime": "2025-06-14T05:00:00Z",
  "arrivalTime": "2025-06-14T15:00:00Z",
  "price": 500.00
  }
  if there is a difference in column airline, departure_airport, 
  departure_time then it will be considered as a new record 
  as these three column is unique 
  if not then it will update the existing record 

### [Delete a Flight](#delete-a-flight)
  DELETE http://localhost:8080/flight/{id}
  example : DELETE /flight/1
  response : Deleted Flight Id : 1

### [Search Flights](#search-flights)
  GET http://localhost:8080/flight/search
  | Parameter            | Required | Description                           |
  | -------------------- | -------- | ------------------------------------- |
  | `airline`            | Yes      | Airline name                          |
  | `supplier`           | Yes      | Data supplier (e.g., CrazySupplier)   |
  | `departureAirport`   | No       | Airport code of departure (e.g., FRA) |
  | `destinationAirport` | No       | Destination airport code (e.g., JFK)  |
  | `departureTime`      | No       | ISO 8601 timestamp (UTC)              |
  | `arrivalTime`        | No       | ISO 8601 timestamp (UTC)              |
  Example Request:
  GET /flight/search?airline=Lufthansa&supplier=CrazySupplier&departureAirport=FRA&departureTime=2025-06-14T05:00:00Z
  Response:
  [
  {
  "flightId": 1,
  "airline": "Lufthansa",
  "supplier": "CrazySupplier",
  "departureAirport": "FRA",
  "destinationAirport": "JFK",
  "departureTime": "2025-06-14T05:00:00Z",
  "arrivalTime": "2025-06-14T15:00:00Z",
  "price": 500.00
  }
  ]

## Date-Time Format
All timestamps should follow the ISO 8601 UTC format:
Correct: 2025-06-14T05:00:00Z
Incorrect: 2025-06-14 05:00:00+00
Internally, the service converts departureTime and arrivalTime to CET (Central European Time) for search filtering. 

## Database Connection (Optional)
You can access the database via PgAdmin:

Field	Value
Host	    localhost
Port	    5433
Database	flightData_db
Username	hand_db_admin [Use your postgres username to login]
Password	welcome123 [Use your postgres password to login]

## Notes
If external API (CrazySupplier) is unreachable, data is mocked.
Uses DTO pattern and Service layer abstraction.
Search criteria uses custom filter object with timezone handling.
