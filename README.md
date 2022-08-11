[![Build Status][build-badge]][build-status]
[![Coverage Status][coverage-badge]][coverage-status]

# Product Management Service

A service designed to connect our Library Service Status System to VersionOne and GitHub developed and maintained by [Texas A&M University Libraries][tamu-library].

<details>
<summary>Table of contents</summary>

  - [Deployment](#deployment)
  - [Developer Documentation](#developer-documentation)
  - [Additional Resources](#additional-resources)

</details>

## Deployment

For a quick and easy deployment using `docker-compose` consider using the [Project Managment App Repo][app-repo].

For _advanced use cases_, or when `docker-compose` is unavailable, the UI may be either started using `docker` directly or even manually started.
This process is further described in the [Deployment Guide][deployment-guide].

<div align="right">(<a href="#readme-top">back to top</a>)</div>

## Developer Documentation

- [Contributors Documentation][contribute-guide]
- [Deployment Documentation][deployment-guide]
- [API Documentation][api-documentation]

<div align="right">(<a href="#readme-top">back to top</a>)</div>

## Additional Resources

Please feel free to file any issues concerning Project Management Service to the issues section of the repository.

Any questions concerning Project Management Service can be directed to helpdesk@library.tamu.edu.

<div align="right">(<a href="#readme-top">back to top</a>)</div>

Copyright © 2022 Texas A&M University Libraries under the [The MIT License][license].

<!-- LINKS -->
[app-repo]: https://github.com/TAMULib/ProjectManagement
[build-badge]: https://github.com/TAMULib/ProjectManagementService/workflows/Build/badge.svg
[build-status]: https://github.com/TAMULib/ProjectManagementService/actions?query=workflow%3ABuild
[coverage-badge]: https://coveralls.io/repos/github/TAMULib/ProjectManagementService/badge.svg
[coverage-status]: https://coveralls.io/github/TAMULib/ProjectManagementService

[api-documentation]: https://tamulib.github.io/ProjectManagementService
[tamu-library]: http://library.tamu.edu
[deployment-guide]: DEPLOYING.md
[contribute-guide]: CONTRIBUTING.md
[license]: LICENSE
