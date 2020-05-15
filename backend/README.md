## Updates
**Backend**
- Nothing so far

**Frontend**
- Updated Angular framework and dependencies to v8.0
- Updated dockerfiles for local testing
- Updated AngularJS environment settings for local testing (using localhost on config)

## Backend
**Prerequisites**
You need to have a JDK installed, i'm using openjdk 11, on ubuntu:
`apt-get install openjdk-11-jdk`
Make sure the JAVA_HOME environment variable is set!

**Building**
`./mvnw clean install`

**Running (outside of container)**
`./mvnw spring-boot:run`

[Use .cmd if on windows]

**Adding a new user**
This command will create the following user:
- username: `neslinesli93`
- password `password`
```bash
curl -H "Authorization: Bearer $(curl register-app:secret@localhost:8080/oauth/token -d "grant_type=client_credentials&client_id=register-app" | jq --raw-output ."access_token")" localhost:8080/api/register -H "Content-Type: application/json" -d '{"username":"neslinesli93","password":"password"}' | jq
```
