# Match Engine API

This project is a fully functional, containerized simulation of a financial exchange's core matching engine. It is built with Spring Boot and uses a PostgreSQL database for data persistence. The entire application stack can be run easily using Docker Compose.

## Features

- **In-Memory Order Books**: For high-performance, real-time order matching.
- **Persistent Database**: All trades, orders, clients, and holdings are persisted in a PostgreSQL database.
- **Data Recovery**: The application loads all open orders from the database on startup to reconstruct the live order books.
- **Dynamic Instruments**: New tradable instruments (stocks) can be added dynamically via an API endpoint.
- **Client and Portfolio Management**: APIs to create clients, manage their balances (cash and stock), and view their portfolios and pending orders.
- **API-First Design**: The API is defined using OpenAPI 3.0 (`api.yaml`) and controllers are generated from this specification.
- **Containerized**: The entire application (Spring Boot app + PostgreSQL database) is designed to run in Docker containers managed by Docker Compose.

---

## How to Run the Application

This project is designed to be run with Docker and Docker Compose. You do not need to have Java or Maven installed on your local machine.

### Prerequisites

- Docker
- Docker Compose

### Running the Application

1.  **Clone the repository** (if you haven't already).

2.  **Navigate to the project root directory** in your terminal. This is the directory containing the `docker-compose.yml` file.

3.  **Build and start the services** using Docker Compose. Use the `--build` flag to ensure the latest code changes are included.

    ```sh
    docker-compose up --build
    ```

    This command will:
    - Build the Spring Boot application from the source using a multi-stage Dockerfile.
    - Start a PostgreSQL database container.
    - Start the Match Engine application container.
    - Connect the two containers on a shared Docker network.

    The application will be available on `http://localhost:8080`.

4.  **To stop the application**, press `Ctrl+C` in the terminal where Docker Compose is running. To stop and remove the containers, you can run:
    ```sh
    docker-compose down
    ```
    Your database data will be preserved in a `db-data` directory in your project folder.

---

## How to Use the API

You can interact with the API using any HTTP client (like `curl`, Postman) or by using the built-in Swagger UI.

**Swagger UI URL**: `http://localhost:8080/swagger-ui.html`

Here are some examples using `curl`:

### 1. Add a New Tradable Instrument

First, you need to add some stocks to the exchange. Let's add `AAPL` and a cash-equivalent, `USDT`.

```sh
# Add AAPL
curl -X POST "http://localhost:8080/instruments" -H "Content-Type: application/json" -d'
{
  "ticker": "AAPL",
  "description": "Apple Inc."
}
'

# Add USDT (for cash balance)
curl -X POST "http://localhost:8080/instruments" -H "Content-Type: application/json" -d'
{
  "ticker": "USDT",
  "description": "Tether USD"
}
'
```

### 2. Create a New Client

Create a client with a unique ID (e.g., `client-123`).

```sh
curl -X POST "http://localhost:8080/clients" -H "Content-Type: application/json" -d'
{
  "clientId": "client-123",
  "name": "John Doe"
}
'
```

### 3. Add Balance for a Client

Give your new client some "cash" (USDT) to trade with.

```sh
curl -X POST "http://localhost:8080/clients/client-123/add-balance" -H "Content-Type: application/json" -d'
{
  "ticker": "USDT",
  "quantity": 100000
}
'
```

### 4. Submit an Order

Now, `client-123` can submit a buy order for `AAPL`.

```sh
curl -X POST "http://localhost:8080/trade" -H "Content-Type: application/json" -d'
{
  "clientId": "client-123",
  "ticker": "AAPL",
  "side": "BUY",
  "quantity": 10,
  "price": 150.00
}
'
```
*(This order will be open and pending as there are no sell orders to match it with yet.)*

### 5. Check Pending Orders

You can see the open order you just created.

```sh
curl -X GET "http://localhost:8080/clients/client-123/pending-orders"
```

### 6. Check Client Portfolio

Check the client's holdings. The USDT balance will be the same since the order hasn't been filled.

```sh
curl -X GET "http://localhost:8080/clients/client-123/portfolio"
```

---

## Project Structure

- `src/main/java`: Contains the main application source code.
  - `api/`: Generated API interfaces from OpenAPI.
  - `model/`: Generated model classes from OpenAPI.
  - `repository/`: Spring Data JPA repositories for database interaction.
  - `*.java`: Core entities, services, and controllers.
- `src/main/resources`:
  - `api.yaml`: The OpenAPI specification for the entire API.
  - `application.properties`: Spring Boot configuration, including database connection details.
- `Dockerfile`: A multi-stage Dockerfile to build and run the application.
- `docker-compose.yml`: Orchestrates the application and PostgreSQL database containers.
- `pom.xml`: The Maven project configuration file.
