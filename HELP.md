# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/4.1.2/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.1.2/gradle-plugin/packaging-oci-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.1.2/reference/web/servlet.html)
* [Spring Security](https://docs.spring.io/spring-boot/4.1.2/reference/web/spring-security.html)
* [Spring Data MongoDB](https://docs.spring.io/spring-boot/4.1.2/reference/data/nosql.html#data.nosql.mongodb)
* [Validation](https://docs.spring.io/spring-boot/4.1.2/reference/io/validation.html)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/4.1.2/reference/actuator/index.html)
* [SpringDoc OpenAPI](https://springdoc.org/)

### TGMagazine Authentication API

Start MongoDB locally, then run the backend with `.\gradlew.bat bootRun`. The project keeps the existing MongoDB database name `Home` and stores users in the `user` collection. Override the connection with `MONGODB_URI` when needed.

For MongoDB Atlas, activate the `cloud` profile and provide `MONGODB_USERNAME` and `MONGODB_PASSWORD` as environment variables; optionally set `MONGODB_DATABASE` (defaults to `Home`). For example, in PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE = "cloud"
$env:MONGODB_USERNAME = "your-atlas-username"
$env:MONGODB_PASSWORD = "your-url-encoded-password"
.\gradlew.bat bootRun
```

URL-encode reserved characters in the password used in the MongoDB URI (for example, `@` as `%40`). Keep credentials in environment variables or a secrets manager, not in committed properties files.

The local JWT secret in `application.properties` is for development only. Set `JWT_SECRET` to a strong secret (at least 32 bytes) before deployment. `JWT_EXPIRATION_MS` sets token lifetime, and `CORS_ALLOWED_ORIGINS` is a comma-separated list of allowed React origins.

### Deploying to Render

1. Push this project to a GitHub repository.
2. In Render, choose **New → Blueprint**, connect the repository, and deploy the `render.yaml` blueprint.
3. When prompted, enter your Atlas database username and password. Use the database user's password (URL-encoded if it contains reserved URI characters), not an Atlas account password. Enter your deployed frontend's origin for `CORS_ALLOWED_ORIGINS`, such as `https://your-frontend.example`; do not include a path or trailing slash.
4. In MongoDB Atlas, add the Render service's outbound IP addresses to the Atlas IP access list. For a Render free instance those addresses are not static; `0.0.0.0/0` allows access from any IP and is less restrictive, so use it only if necessary and protect the database with a strong unique password.
5. After deployment, use the Render service URL as the backend base URL in the frontend.

The Render blueprint builds and runs the app with Docker, uses Render's `PORT`, enables the `cloud` profile, and generates a `JWT_SECRET`. Keep all credentials in Render environment variables; never commit them.

* `POST /api/auth/signup` — JSON: `{"fullName":"Pavan","email":"user@gmail.com","password":"password123"}`. New public signups receive the `JOB_SEEKER` role.
* `POST /api/auth/login` — JSON: `{"email":"user@gmail.com","password":"password123"}`. The response includes a bearer token.
* `GET /api/welcome` and `GET /api/user/profile` — send `Authorization: Bearer <token>`.
* Logout is client-side: discard the token.

Signup returns `201 Created`; invalid input returns `400`, duplicate email returns `409`, and failed login or missing/invalid authentication returns `401`.

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [SpringDoc OpenAPI](https://github.com/springdoc/springdoc-openapi-demos/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)
