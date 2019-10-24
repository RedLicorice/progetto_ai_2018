## Prerequisites
MongoDB installed and running on port 27017

## Build and run
`./mvwn clean install && ./mvnw spring-boot:run`

## Add new user
This command will create the following user:
- username: `neslinesli93`
- password `password`

```bash
curl -H "Authorization: Bearer $(curl register-app:secret@localhost:8080/oauth/token -d "grant_type=client_credentials&client_id=register-app" | jq --raw-output ."access_token")" localhost:8080/api/register -H "Content-Type: application/json" -d '{"username":"neslinesli93","password":"password"}' | jq
```
