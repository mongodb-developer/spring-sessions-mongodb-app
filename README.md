# Spring Session MongoDB Example

This repository contains a simple Spring Boot application demonstrating how to use **Spring Session MongoDB** to store HTTP session data in MongoDB.

Instead of storing session state inside an application container, Spring Session MongoDB persists session data in MongoDB. This allows multiple application instances to share the same session state, making it suitable for distributed or load-balanced environments.

The example application stores a user's **theme preference** (`light` or `dark`) in a session to illustrate how sessions are created, persisted, and reused across requests.

---

# How It Works

The application exposes two endpoints:

```
POST /theme
GET  /theme
```

The flow works as follows:

1. A client sends a request to set the theme.
2. Spring creates a new `HttpSession` if one does not exist.
3. The theme value is stored as a session attribute.
4. Spring Session persists the session document in MongoDB.
5. The server returns a `SESSION` cookie to the client.
6. Future requests include the cookie, allowing Spring to resolve the same session from MongoDB.

This demonstrates how session state can be externalized from the application server and stored in MongoDB.

---

# Project Structure

```
src/main/java/com/mongodb/springsessionsmongodbapp

SpringSessionsMongodbAppApplication.java
    Spring Boot application entry point

SessionConfig.java
    Enables MongoDB-backed HTTP sessions

ThemeController.java
    Example API that stores and retrieves a session attribute
```

---

# Requirements

* Java 17+
* Maven
* MongoDB (local or MongoDB Atlas)

---

# Configuration

The application expects a MongoDB connection string through an environment variable:

```
MONGODB_URI
```

Example:

```
export MONGODB_URI="mongodb+srv://<username>:<password>@cluster.mongodb.net/?retryWrites=true&w=majority"
```

The application configuration appends the database name and application name automatically:

```
spring.application.name=spring-sessions-mongodb-app

spring.mongodb.database=springSessions
spring.mongodb.uri=${MONGODB_URI}&appName=${spring.application.name}
```

If your URI does **not** contain query parameters, use:

```
spring.mongodb.uri=${MONGODB_URI}?appName=${spring.application.name}
```

---

# Running the Application

Start the application using Maven:

```
mvn spring-boot:run
```

The API will start on:

```
http://localhost:8080
```

---

# Testing the Session Flow

Create a session and set a theme:

```
curl -i -c cookies.txt -X POST "http://localhost:8080/theme?theme=light"
```

Example response:

```
HTTP/1.1 200
Set-Cookie: SESSION=...
Content-Type: application/json
```

```
{
  "sessionId": "...",
  "theme": "light",
  "message": "Theme set"
}
```

The cookie is saved to `cookies.txt`.

---

Retrieve the theme using the same session:

```
curl -i -b cookies.txt http://localhost:8080/theme
```

Example response:

```
{
  "sessionId": "...",
  "theme": "light"
}
```

Because the same cookie is used, the session is resolved from MongoDB and the stored theme value is returned.

---

# Inspecting Sessions in MongoDB

Sessions are stored as documents in the configured database.

Example session document:

```json
{
  "_id": "4321d619-8526-4ca2-8163-32d09b12ee98",
  "created": { "$date": "2026-03-12T14:24:11.341Z" },
  "accessed": { "$date": "2026-03-12T14:24:15.733Z" },
  "interval": "PT30M",
  "principal": null,
  "expireAt": { "$date": "2026-03-12T14:54:15.733Z" },
  "attr": { "$binary": "..." }
}
```

MongoDB maintains a **TTL index on the `expireAt` field**, which automatically removes expired sessions.

This allows session lifecycle management to happen automatically without additional application logic.

---

# Why Use MongoDB for Sessions

MongoDB works well as a session store because:

* sessions map naturally to document structures
* TTL indexes automatically remove expired sessions
* the database scales horizontally for high traffic environments
* session data can be shared across multiple application instances

This makes MongoDB a strong foundation for managing sessions in distributed applications.

---

# Learn More

* Spring Session:
  [https://spring.io/projects/spring-session](https://spring.io/projects/spring-session)

* Spring Session MongoDB Integration:
  [https://www.mongodb.com/docs/drivers/java/sync/current/integrations/spring-session/](https://www.mongodb.com/docs/drivers/java/sync/current/integrations/spring-session/)

* MongoDB Atlas:
  [https://www.mongodb.com/cloud/atlas](https://www.mongodb.com/cloud/atlas)
