[![Build Status](https://travis-ci.org/TAMULib/ProductManagementService.svg?branch=master)](https://travis-ci.org/TAMULib/ProductManagementService) [![Coverage Status](https://coveralls.io/repos/github/TAMULib/ProductManagementService/badge.svg?branch=master)](https://coveralls.io/github/TAMULib/ProductManagementService?branch=master)

# Product Management Service
The Product Management Service is designed to connect our Library Webservice Status Application to our Version Managagement Software.

## Building Product Management Service

### Development
```bash
$ mvn clean spring-boot:run
```

### Production
```bash
$ mvn clean package -DskipTests -Dproduction
```

## Rest API

| **Title**            | **Products**                                                                                |
| :------------------- | :------------------------------------------------------------------------------------------ |
| **Description**      | Returns a list of all products.                                                             |
| **URL**              | ```/products```                                                                             |
| **Method**           | **GET**                                                                                     |
| **URL Parameters**   |                                                                                             |
| **Success Response** | **Code:** 200 OK<br/>**Content Type:** application/json<br/>                                |
| **Sample Request**   | ```/products```                                                                             |
| **Notes**            | These are managed products for this service and not products from a remote product manager. |

```json
{
  "meta": {
    "status": ApiStatus,
    "action": ApiAction,
    "message": String,
    "id": String
  },
  "payload": {
    "ArrayList<Product>": [
      {
        "id": Long,
        "name": String,
        "scopeId": String,
        "remoteProductManager": {
          "id": Long,
          "name": String,
          "type": String
        }
      }
    ]
  }
}
```

<hr />

| **Title**            | **Active Sprints**                                                                                                                                                                                                                                        |
| :------------------- | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Description**      | Returns a list of all active sprints based on associated remote product manager products.                                                                                                                                                                 |
| **URL**              | ```/sprints/active```                                                                                                                                                                                                                                     |
| **Method**           | **GET**                                                                                                                                                                                                                                                   |
| **URL Parameters**   |                                                                                                                                                                                                                                                           |
| **Success Response** | **Code:** 200 OK<br/>**Content Type:** application/json<br/>**Schema:**                                                                                                                                                                                   |
| **Sample Request**   | ```/sprints/active```                                                                                                                                                                                                                                     |
| **Notes**            | Currently, VersionOne is the only remote product manager implemented. VersionOne sprints are based on a timebox which is a sprint schedule in the UI. Products can share the same sprint schedule and will appear to be the same sprint in this response. |

```json
{
  "meta": {
    "status": ApiStatus,
    "action": ApiAction,
    "message": String,
    "id": String
  },
  "payload": {
    "ArrayList<Sprint>": [
      {
        "id": String,
        "name": String,
        "product": String,
        "cards": [
          {
            "id": String,
            "number": String,
            "type": String,
            "status": String,
            "name": String,
            "description": String,
            "estimate": Float,
            "assignees": [
              {
                "id": String,
                "name": String,
                "avatar": String
              }
            ]
          }
        ]
      }
    ]
  }
}
```

<hr />

| **Title**            | **Products Stats**                                                                                                  |
| :------------------- | :------------------------------------------------------------------------------------------------------------------ |
| **Description**      | Returns a list of all products with statistics gathered from their associated remote product manager products.      |
| **URL**              | ```/products/stats```                                                                                               |
| **Method**           | **GET**                                                                                                             |
| **URL Parameters**   |                                                                                                                     |
| **Success Response** | **Code:** 200 OK<br/>**Content Type:** application/json                                                             |
| **Sample Request**   | ```/products/stats```                                                                                               |
| **Notes**            |                                                                                                                     |

```json
{
  "meta": {
    "status": ApiStatus,
    "action": ApiAction,
    "message": String,
    "id": String
  },
  "payload": {
    "ArrayList<ProductStats>": [
      {
        "id": String,
        "name": String,
        "requestCount": Interger,
        "issueCount": Interger,
        "featureCount": Interger,
        "defectCount": Interger,
        "backlogItemCount": Interger
      }
    ]
  }
}
```
