# Airline Reservation API

Backend REST API for the **CS492 Group 3 Airline Reservation System**. The API is built with Java and Spring Boot and runs with its database through Docker Compose.

## Repository

[View the Airline Reservation API source code on GitHub](https://github.com/Rickydiaz30/airlineReservatioinApi)

## Sprint 1 Status

The API foundation, authentication endpoint, database connection, and container configuration are included in Sprint 1.

The Angular user interface contains the currently demonstrated reservation workflow. Additional API business logic and complete UI integration will continue during future sprints.

## Technology Stack

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Security
- Maven
- Docker
- Docker Compose
- Relational database

## Project Structure

```text
.
├── .mvn/                  Maven Wrapper support files
├── src/                   Application source code and tests
├── .dockerignore          Docker build exclusions
├── .env.example           Environment-variable template
├── compose.yaml           API and database services
├── Dockerfile             API container instructions
├── mvnw                    Maven Wrapper for macOS and Linux
├── mvnw.cmd                Maven Wrapper for Windows
├── pom.xml                 Maven configuration
└── README.md               Project documentation
```

## Prerequisites

Install the following:

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Git](https://git-scm.com/)
- Postman or another API-testing application

Java and Maven do not need to be installed on the host computer when the application is run entirely through Docker Compose.

## Clone the Repository

Open PowerShell or another terminal and run:

```powershell
git clone https://github.com/Rickydiaz30/airlineReservatioinApi.git
cd airlineReservatioinApi
```

If the project was provided as part of the assignment ZIP, extract it and open a terminal in the API folder containing `compose.yaml`.

## Configure the Environment

Copy the included environment template to create a local `.env` file.

### Windows PowerShell

```powershell
Copy-Item .env.example .env
```

### macOS or Linux

```bash
cp .env.example .env
```

Open `.env` and provide the local database configuration required by `compose.yaml`.

The Spring Boot application uses these environment variables:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

Use `.env.example` as the authoritative template if it contains additional variables required by the database container.

> Never commit the real `.env` file. Do not place real passwords, API keys, database credentials, JWT secrets, or other private information in the repository.

## Run the Project with Docker Compose

Make sure Docker Desktop is running. From the directory containing `compose.yaml`, execute:

```powershell
docker compose up --build
```

Docker Compose will:

- Build the Spring Boot API image
- Start the database container
- Start the API container
- Create the required Docker network
- Load the configured environment variables

The first build may take several minutes while Docker downloads the required images and Maven dependencies.

Wait for the console to indicate that Spring Boot and Tomcat have started successfully.

## Access the API

The API is available from the host computer at:

```text
http://localhost:8081
```

Spring Boot listens on port `8080` inside the API container. Docker maps port `8081` on the host computer to port `8080` inside the container:

```text
localhost:8081 → container:8080
```

The Spring Boot configuration remains:

```properties
server.port=${PORT:8080}
```

The external port is controlled by the port mapping in `compose.yaml`.

## Confirm the Containers Are Running

Open another terminal in the API project directory and run:

```powershell
docker compose ps
```

The API and database services should both show as running.

View the container logs with:

```powershell
docker compose logs
```

Follow the logs continuously with:

```powershell
docker compose logs -f
```

Press `Ctrl+C` to stop following the logs.

## Test the Login Endpoint

The authentication endpoint can be tested with Postman.

### Request

```text
POST http://localhost:8081/api/auth/login
```

In Postman:

1. Select the `POST` request method.
2. Enter the login endpoint URL.
3. Select **Body**.
4. Select **raw**.
5. Select **JSON** as the content type.
6. Enter the login credentials expected by the application.
7. Select **Send**.

The request must include this header:

```text
Content-Type: application/json
```

A successful login returns:

```text
200 OK
```

The response contains the authenticated user’s non-sensitive account information, such as the user ID, name, email address, and role.

## Stop the Application

If Docker Compose is running in the foreground, press:

```text
Ctrl+C
```

Then stop and remove the containers and Docker network:

```powershell
docker compose down
```

This normally preserves data stored in a named database volume.

To stop the containers without removing them:

```powershell
docker compose stop
```

Start the existing containers again with:

```powershell
docker compose start
```

## Rebuild After Code Changes

After changing Java source code or project dependencies, rebuild and restart the application:

```powershell
docker compose up --build
```

To force Docker to build a completely new API image:

```powershell
docker compose build --no-cache
docker compose up
```

## Run Tests Locally

If Java is installed locally, run tests with the included Maven Wrapper.

### Windows

```powershell
.\mvnw.cmd test
```

### macOS or Linux

```bash
./mvnw test
```

## Build the Application Locally

### Windows

```powershell
.\mvnw.cmd clean package
```

### macOS or Linux

```bash
./mvnw clean package
```

The generated JAR file will be placed in the `target` directory.

The `target` directory contains generated build output and should not be committed or included in the source-code submission.

## Angular UI Integration

The Angular frontend is maintained in a separate project.

When the UI and API are both running locally, the UI should send API requests to:

```text
http://localhost:8081
```

The deployed Sprint 1 UI is available at:

[https://airlinereservationgroup3.netlify.app](https://airlinereservationgroup3.netlify.app)

During Sprint 1, the deployed UI demonstrates the planned application functionality while further API integration remains under development.

## Troubleshooting

### Docker Is Not Recognized

Install and start Docker Desktop. Open a new terminal and verify the installation:

```powershell
docker --version
docker compose version
```

### Port 8081 Is Already in Use

Stop the application currently using port `8081`, or change only the host side of the port mapping in `compose.yaml`.

For example:

```yaml
ports:
  - "8082:8080"
```

The API would then be available at:

```text
http://localhost:8082
```

### Database Connection Fails

Confirm that:

- Docker Desktop is running.
- The `.env` file exists beside `compose.yaml`.
- The variable names match `.env.example` and `compose.yaml`.
- The database container is running.
- The database username and password are correct.
- `DB_URL` uses the Docker database service name instead of `localhost` when the API runs inside Docker.

Check the container status and logs:

```powershell
docker compose ps
docker compose logs
```

### Code Changes Do Not Appear

Rebuild the API container:

```powershell
docker compose down
docker compose up --build
```

### Reset the Local Database

The following command deletes the containers and named volumes, including locally stored database data:

```powershell
docker compose down -v
```

Only use this command when permanently resetting the local database is intentional.

After resetting the database, rebuild the project:

```powershell
docker compose up --build
```

## Development Workflow

Development work is performed on the `dev` branch. Completed changes are merged into the protected `main` branch through GitHub pull requests.

Before starting work:

```powershell
git pull origin dev
```

To save and push changes:

```powershell
git add .
git status
git commit -m "Describe the changes"
git push origin dev
```

On GitHub, create a pull request with:

```text
base: main
compare: dev
```

Do not push directly to the protected `main` branch.

## Security

- Never commit `.env`.
- Keep `.env` listed in `.gitignore`.
- Do not store passwords or private credentials in source files.
- Do not place real credentials in `.env.example`.
- Do not commit API keys, JWT secrets, SMTP credentials, or database passwords.
- Use environment variables for sensitive configuration.
- Do not include `.idea`, `.git`, `target`, logs, or generated files in the submitted source-code copy.

## Team

**CS492 Group 3**  
Airline Reservation System