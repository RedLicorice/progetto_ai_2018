# REST API
The REST API is exposed on port 8080, an application proxy such as traefik is needed for routing in production.
Authentication is based on OAUTH2 protocol, using JWT tokens.

## Authentication endpoints
### Application token
`POST` on `/oauth/token`, using parameters:

```
{
    "grant-type":"client_credentials",
    "client_id"="register-app"
}
```

---
### Login (get token)
`POST` on `/oauth/token` with request body (as form data):
```
{
   "grant_type":"password",
   "username":"neslinesli93",
   "password":"password"
}
```


## User endpoints
### Current user profile
`GET` on `/me` to get information about current user.

---
### User and Administrator registration
`POST` on `/register` 

`POST` on `/register-admin` 

with request body:
```
{ 
    "username": "your-username", 
    "password": "your-password" 
}
```

Note that the "register-admin" endpoint only works for the first administrator.

## Archive Endpoints

### Archives summary
`GET` on `/archives` to get a summary of all of the user-owned archives (both uploaded and purchased)

---
### Upload an archive
`POST` on `/archives/upload` to upload a new archive, request body must be a list of "Measure" resources:

```
[
    [...]
    {"latitude":38.12233, "longitude":15.65334, "timestamp":1592324706}
]
```

---
### Uploaded archives summary
`GET` on `/archives/upload` to get a summary of all of the user's uploaded archives (with details)

---
### Download an archive
`GET` on `/archives/download/{archiveId}` to download an archive (must be either owned or uploaded by the user). It returns the full resource for said archive.

---
### Preview an archive
`GET` on `/archives/public/{archiveId}` to get a preview of an archive's contents, it returns the approximated resource for said archive.

---
### Delete an uploaded archive
`DELETE` on `/archives/{archiveId}` to mark the specified archive as deleted, so that it is not available for further purchases.

---
### Archive search
`POST` on `/archives/search` to search for archives in an area, request body must
include the top-left and bottom-right corners of the map viewport, and any (optional) filters.
```
{
    "topLeft": { "longitude" : 38.12386, "latitude" : 15.65228 },
    "bottomRight": {"longitude" : 38.12154, "latitude" : 15.65676 },
    "from": null,
    "to": null,
    "users": []
}
```

---
### Archive purchase
`POST` on `/archives/buy` to generate invoices for purchasing one or more archives.
Each invoice refers to a single archive and expires after 24 hours.
Request body should be a list of the archive id's to be purchased.

```
[
    "archive1",
    "archive2",
    "archive3",
]
```

## Store endpoints
### Invoices summary
`GET` on `/store/invoices` to list all of user's invoices.

Unpaid invoices expire after 24 hours, expired invoices are deleted at the next request to avoid clutter.

### Invoice detail
`GET` on `/store/invoices/{invoiceId}` to see details of a specific invoice.

### Pay invoice
`POST` on `/store/invoices/{invoiceId}` to pay a specific invoice.
Invoices are paid individually to give the user a second chance to review his purchase.





