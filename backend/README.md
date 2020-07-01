## Backend
This project is based on Spring boot and MongoDB, and exposes a REST API whose endpoints are described [here](REST_API.md)
### Prerequisites

You need to have a JDK installed, i'm using openjdk 11, on ubuntu:
`apt-get install openjdk-11-jdk`
Make sure the JAVA_HOME environment variable is set!

###Building
`./mvnw clean install`

### Running (outside of container)
`./mvnw spring-boot:run`

[Use .cmd if on windows]

[!]Make sure you have a running instance of Mongodb!

You can use mongodb.yml in the repository root for that, it'll take care of everything.

`docker-compose -f mongodb.yml up -d`
### Adding a new user
This command will create the following user:
- username: `neslinesli93`
- password `password`
```bash
curl -H "Authorization: Bearer $(curl register-app:secret@localhost:8080/oauth/token -d "grant_type=client_credentials&client_id=register-app" | jq --raw-output ."access_token")" localhost:8080/api/register -H "Content-Type: application/json" -d '{"username":"neslinesli93","password":"password"}' | jq
```
This same request is included in the postman collection.
### Testing
Postman has been used for testing, import the [request collection](Progetto_AI.postman_collection.json) and [environment](Progetto_AI_ENV.postman_environment.json) to quickly debug the endpoints in a headless fashion.

## Updates
- Changed class hierarchy to suit Archives (and not single positions)
- Adjusted the API according to the refactored hierarchy
- Added API Documentation
