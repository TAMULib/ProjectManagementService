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