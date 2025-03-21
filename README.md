# Online Banking App

Online Banking Application to show secure architecture and secure coding

## Table of Contents


## Prerequisites

## Sonarqube

### Sonarqube Installation

Run the following command in the folder `sonarqube` to start the Sonarqube server:

```bash
./start.sh
```

### Sonarqube Configuration and Analysis

1. Open the browser and navigate to `http://localhost:9000/`
2. Login with the default credentials `admin/admin`
3. Change the default password
4. Create a new local project
5. Generate a token for the project
6. Update the `sonar-project.properties` file with the generated token
7. Run the following command to analyze the `banking-backend` project and send the results to the Sonarqube server:

```bash
./mvnw clean verify sonar:sonar \
  -Dsonar.projectKey=banking-backend \
  -Dsonar.projectName='Banking Backend' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_29676385587287a00fe652edf9412ff33701c0c9
```
