[![Build Status](https://travis-ci.org/TAMULib/ProjectManagementService.svg?branch=master)](https://travis-ci.org/TAMULib/ProjectManagementService) [![Coverage Status](https://coveralls.io/repos/github/TAMULib/ProjectManagementService/badge.svg?branch=master)](https://coveralls.io/github/TAMULib/ProjectManagementService?branch=master)

# Project Management Service
The Project Management Service is designed to connect our Library Webservice Status Application to our Version Managagement Software.

## Building Project Management Service

### Development
```bash
$ mvn clean spring-boot:run
```

### Production
```bash
$ mvn clean package -DskipTests -Dproduction
```

## Rest API

| **Title**            | **Projects**                                                                                |
| :------------------- | :------------------------------------------------------------------------------------------ |
| **Description**      | Returns a list of all projects.                                                             |
| **URL**              | ```/projects```                                                                             |
| **Method**           | **GET**                                                                                     |
| **URL Parameters**   |                                                                                             |
| **Success Response** | **Code:** 200 OK<br/>**Content Type:** application/json<br/>                                |
| **Sample Request**   | ```/projects```                                                                             |
| **Notes**            | These are managed projects for this service and not projects from a remote project manager. |

```json
{
  "meta": {
    "status": ApiStatus,
    "action": ApiAction,
    "message": String,
    "id": String
  },
  "payload": {
    "ArrayList<Project>": [
      {
        "id": Long,
        "name": String,
        "scopeId": String,
        "remoteProjectManager": {
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
| **Description**      | Returns a list of all active sprints based on associated remote project manager projects.                                                                                                                                                                 |
| **URL**              | ```/sprints/active```                                                                                                                                                                                                                                     |
| **Method**           | **GET**                                                                                                                                                                                                                                                   |
| **URL Parameters**   |                                                                                                                                                                                                                                                           |
| **Success Response** | **Code:** 200 OK<br/>**Content Type:** application/json<br/>**Schema:**                                                                                                                                                                                   |
| **Sample Request**   | ```/sprints/active```                                                                                                                                                                                                                                     |
| **Notes**            | Currently, VersionOne is the only remote project manager implemented. VersionOne sprints are based on a timebox which is a sprint schedule in the UI. Projects can share the same sprint schedule and will appear to be the same sprint in this response. |

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
        "project": String,
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

| **Title**            | **Projects Stats**                                                                                                  |
| :------------------- | :------------------------------------------------------------------------------------------------------------------ |
| **Description**      | Returns a list of all projects and there statistics gathered from their associated remote project manager projects. |
| **URL**              | ```/projects/stats```                                                                                               |
| **Method**           | **GET**                                                                                                             |
| **URL Parameters**   |                                                                                                                     |
| **Success Response** | **Code:** 200 OK<br/>**Content Type:** application/json                                                             |
| **Sample Request**   | ```/projects/stats```                                                                                               |
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
    "ArrayList<ProjectStats>": [
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