[![Build Status](https://github.com/TAMULib/ProjectManagementService/workflows/Build/badge.svg)](https://github.com/TAMULib/ProjectManagementService/actions?query=workflow%3ABuild)
[![Coverage Status](https://coveralls.io/repos/github/TAMULib/ProjectManagementService/badge.svg)](https://coveralls.io/github/TAMULib/ProjectManagementService)
[![GHPages Status](https://github.com/TAMULib/ProjectManagementService/workflows/GHPages/badge.svg)](https://github.com/TAMULib/ProjectManagementService/actions?query=workflow%3AGHPages)

# Product Management Service
The Product Management Service is designed to connect our Library Webservice Status Application to our Version Management Software.

## Building Product Management Service

### Development
```bash
$ mvn clean spring-boot:run
```

### Production
```bash
$ mvn clean package -DskipTests -Dproduction
```

## Developer Documentation

- [API Documentation](https://tamulib.github.io/ProjectManagementService)

