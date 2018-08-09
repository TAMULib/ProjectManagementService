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
    "status": "SUCCESS",
    "action": null,
    "message": "Your request was successful",
    "id": null
  },
  "payload": {
    "ArrayList<Project>": [
      {
        "id": 1,
        "name": "Legacy DSpace",
        "scopeId": "1934",
        "remoteProjectManager": {
          "id": 1,
          "name": "VersionOne",
          "type": "VERSION_ONE"
        }
      },
      {
        "id": 2,
        "name": "Code Management - Maps",
        "scopeId": "3781",
        "remoteProjectManager": {
          "id": 1,
          "name": "VersionOne",
          "type": "VERSION_ONE"
        }
      },
      {
        "id": 3,
        "name": "CORAL - Electronic Resource Management",
        "scopeId": "3783",
        "remoteProjectManager": {
          "id": 1,
          "name": "VersionOne",
          "type": "VERSION_ONE"
        }
      },
      {
        "id": 4,
        "name": "Piper - Automated Ingest",
        "scopeId": "3786",
        "remoteProjectManager": {
          "id": 1,
          "name": "VersionOne",
          "type": "VERSION_ONE"
        }
      }
    ]
  }
}
```

<hr />

| **Title**            | **Active Sprints**                                                                                                                                                                                                                                    |
| :------------------- | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Description**      | Returns a list of all active sprints based on associated remote project manager projects.                                                                                                                                                             |
| **URL**              | ```/sprints/active```                                                                                                                                                                                                                                 |
| **Method**           | **GET**                                                                                                                                                                                                                                               |
| **URL Parameters**   |                                                                                                                                                                                                                                                       |
| **Success Response** | **Code:** 200 OK<br/>**Content Type:** application/json<br/>**Schema:**                                                                                                                                                                               |
| **Sample Request**   | ```/sprints/active```                                                                                                                                                                                                                                 |
| **Notes**            | Currently, only remote project manager implemented is VersionOne. VersionOne sprints are based on a timebox which is a spring schedule in the UI. Projects can share the same sprint schedule and will appear to be the same sprint in this response. |

```json
{
  "meta": {
    "status": "SUCCESS",
    "action": null,
    "message": "Your request was successful",
    "id": null
  },
  "payload": {
    "ArrayList<Sprint>": [
      {
        "id": "8416",
        "name": "Sprint 14",
        "project": "CORAL - Electronic Resource Management",
        "cards": [
          {
            "id": "8234",
            "number": "B-03467",
            "type": "Feature",
            "status": "Accepted",
            "name": "Update new feedback and purchase forms to incorporate all fields from the existing feedback form",
            "description": "<p>Needs to have the new styling and needs to pass WAVE ADA check.</p>\n<p> </p>\n<p>All the key/value information will go right into the Notes field.</p>\n<p> </p>\n<p>We will be finishing out and fully styling the feedback form, the request a purchase form, and ideally the https://coral.library.tamu.edu/resourcelink.php?resource=1440 link resolver while we're at it.</p>",
            "assignees": [
              {
                "id": "20",
                "name": "Jeremy Huff",
                "avatar": "1706"
              },
              {
                "id": "3483",
                "name": "Jason Savell",
                "avatar": "no_avatar.png"
              },
              {
                "id": "7888",
                "name": "Kevin Day",
                "avatar": "no_avatar.png"
              }
            ]
          },
          {
            "id": "8417",
            "number": "B-03578",
            "type": "Feature",
            "status": "Done",
            "name": "Sort by title by default when viewing resource list with trial/purchase requests hidden",
            "assignees": [
              {
                "id": "3483",
                "name": "Jason Savell",
                "avatar": "no_avatar.png"
              }
            ]
          }
        ]
      },
      {
        "id": "8435",
        "name": "Weaver Upgrades/Auth2 Retirement",
        "project": "DI Internal",
        "cards": [
          {
            "id": "8436",
            "number": "B-03587",
            "type": "Feature",
            "status": "Done",
            "name": "Upgrade My Library UI to weaver-ui 2",
            "estimate": 3,
            "assignees": [
              {
                "id": "6616",
                "name": "Ryan Laddusaw",
                "avatar": "no_avatar.png"
              }
            ]
          },
          {
            "id": "8437",
            "number": "B-03588",
            "type": "Feature",
            "status": "Done",
            "name": "Update My Library Service to Weaver 2",
            "estimate": 2,
            "assignees": [
              {
                "id": "6616",
                "name": "Ryan Laddusaw",
                "avatar": "no_avatar.png"
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
    "status": "SUCCESS",
    "action": null,
    "message": "Your request was successful",
    "id": null
  },
  "payload": {
    "ArrayList<ProjectStats>": [
      {
        "id": "1",
        "name": "Legacy DSpace",
        "requestCount": 22,
        "issueCount": 41,
        "featureCount": 32,
        "defectCount": 0,
        "backlogItemCount": 32
      },
      {
        "id": "2",
        "name": "Code Management - Maps",
        "requestCount": 0,
        "issueCount": 0,
        "featureCount": 5,
        "defectCount": 0,
        "backlogItemCount": 5
      },
      {
        "id": "3",
        "name": "CORAL - Electronic Resource Management",
        "requestCount": 2,
        "issueCount": 0,
        "featureCount": 12,
        "defectCount": 8,
        "backlogItemCount": 20
      }
    ]
  }
}
```